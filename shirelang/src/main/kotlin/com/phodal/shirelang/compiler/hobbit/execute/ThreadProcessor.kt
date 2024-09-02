package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.execution.process.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts.TabTitle
import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.readText
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.sh.psi.ShFile
import com.intellij.sh.run.ShRunner
import com.intellij.terminal.TerminalExecutionConsole
import com.intellij.terminal.ui.TerminalWidget
import com.intellij.ui.content.Content
import com.phodal.shirecore.provider.http.HttpHandler
import com.phodal.shirecore.provider.http.HttpHandlerType
import com.phodal.shirecore.provider.shire.FileRunService
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.compiler.SHIRE_ERROR
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.ShireProcessHandler
import com.phodal.shirelang.utils.lookupFile
import java.util.concurrent.CompletableFuture

object ThreadProcessor {
    fun execute(
        myProject: Project, fileName: String, variables: Array<String>, variableTable: MutableMap<String, Any?>,
    ): String {
        val file = myProject.lookupFile(fileName) ?: return "File not found: $fileName"

        val filename = file.name.lowercase()
        val content = file.readText()

        // if ends with .cURL.sh, try call cURL service
        if (filename.endsWith(".curl.sh")) {
            val execute = HttpHandler.provide(HttpHandlerType.CURL)?.execute(myProject, content)
            if (execute != null) {
                return execute
            }
        }

        val psiFile = ReadAction.compute<PsiFile, Throwable> {
            PsiManager.getInstance(myProject).findFile(file)
        } ?: return "Failed to find PSI file for $fileName"

        when (psiFile) {
            is ShireFile -> {
                val psi = psiFile as ShireFile
                return when (val output = variableTable["output"]) {
                    is List<*> -> {
                        val results = output.mapNotNull {
                            try {
                                variableTable["output"] = it
                                executeTask(myProject, variables, variableTable, psi)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        results.joinToString("\n")
                    }

                    is Array<*> -> {
                        output.joinToString("\n") {
                            variableTable["output"] = it
                            executeTask(myProject, variables, variableTable, psi)  ?: "No run service found"
                        }
                    }

                    else -> {
                        return executeTask(myProject, variables, variableTable, psi) ?: "No run service found"
                    }
                }
            }
            is ShFile -> {
                val virtualFile = psiFile.virtualFile
                val workingDirectory = virtualFile.parent.path
                val shRunner = ApplicationManager.getApplication().getService(ShRunner::class.java)
                    ?: return "$SHIRE_ERROR: Shell runner not found"

                ApplicationManager.getApplication().invokeAndWait {
                    if (shRunner.isAvailable(myProject)) {
                        shRunner.run(myProject, virtualFile.path, workingDirectory, "RunShireShell", true)
                    }
                }

                return "Running shell command: $fileName"
            }

            else -> return FileRunService.provider(myProject, file)?.runFile(myProject, file, psiFile)
                ?: "No run service found for $psiFile, $fileName"
        }
    }

    private fun executeShFile(
        psiFile: ShFile,
        myProject: Project,
        fileName: String,
    ): String {
        val virtualFile = psiFile.virtualFile
        val workingDirectory = virtualFile.parent.path
        val shRunner = ApplicationManager.getApplication().getService(ShRunner::class.java)
            ?: return "$SHIRE_ERROR: Shell runner not found"

        val future = CompletableFuture<String>()
        ApplicationManager.getApplication().invokeAndWait {
            if (shRunner.isAvailable(myProject)) {
                val processHandler = ShireProcessHandler(fileName)
                ProcessTerminatedListener.attach(processHandler)

                shRunner.run(myProject, virtualFile.path, workingDirectory, "RunShireShell", false)

                val processAdapter = object : ProcessAdapter() {
                    override fun processTerminated(event: ProcessEvent) {
                        future.complete(event.text)
                    }
                }

                processHandler.addProcessListener(processAdapter)

                val console: TerminalExecutionConsole = TerminalExecutionConsole(myProject, processHandler)
                console.attachToProcess(processHandler)

                processHandler.startNotify()
            }
        }

        return future.get()
    }

    private fun executeTask(
        myProject: Project,
        variables: Array<String>,
        variableTable: MutableMap<String, Any?>,
        psiFile: ShireFile,
    ): String? {
        val executeResult = ShireRunFileAction.suspendExecuteFile(myProject, variables, variableTable, psiFile)
        return executeResult
    }

}
