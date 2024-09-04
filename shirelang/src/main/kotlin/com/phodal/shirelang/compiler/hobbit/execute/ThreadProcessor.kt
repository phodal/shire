package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.sh.psi.ShFile
import com.intellij.sh.run.ShRunner
import com.phodal.shirecore.provider.http.HttpHandler
import com.phodal.shirecore.provider.http.HttpHandlerType
import com.phodal.shirecore.provider.shire.FileRunService
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.compiler.SHIRE_ERROR
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.utils.lookupFile
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture


object ThreadProcessor {
    fun execute(
        myProject: Project, fileName: String, variablesName: Array<String>, variableTable: MutableMap<String, Any?>,
    ): String {
        val file = myProject.lookupFile(fileName) ?: return "File not found: $fileName"

        val filename = file.name.lowercase()
        val content = file.readText()

        // if ends with .cURL.sh, try call cURL service
        if (filename.endsWith(".curl.sh")) {
            val execute = HttpHandler.provide(HttpHandlerType.CURL)
                ?.execute(myProject, content, variablesName, variableTable)
            if (execute != null) {
                return execute
            }
        }

        val psiFile = ReadAction.compute<PsiFile, Throwable> {
            PsiManager.getInstance(myProject).findFile(file)
        } ?: return "Failed to find PSI file for $fileName"

        when (psiFile) {
            is ShireFile -> {
                return when (val output = variableTable["output"]) {
                    is List<*> -> {
                        val results = output.mapNotNull {
                            try {
                                variableTable["output"] = it
                                executeTask(myProject, variablesName, variableTable, psiFile)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        results.joinToString("\n")
                    }

                    is Array<*> -> {
                        output.joinToString("\n") {
                            variableTable["output"] = it
                            executeTask(myProject, variablesName, variableTable, psiFile) ?: "No run service found"
                        }
                    }

                    else -> {
                        return executeTask(myProject, variablesName, variableTable, psiFile) ?: "No run service found"
                    }
                }
            }

            is ShFile -> {
                return executeShFile(psiFile, myProject)
            }

            else -> return FileRunService.provider(myProject, file)?.runFile(myProject, file, psiFile)
                ?: "No run service found for $psiFile, $fileName"
        }
    }

    private fun executeShFile(psiFile: ShFile, myProject: Project): String {
        val virtualFile = psiFile.virtualFile
        val shRunner = ApplicationManager.getApplication().getService(ShRunner::class.java)
            ?: return "$SHIRE_ERROR: Shell runner not found"

        val future = CompletableFuture<String>()
        ApplicationManager.getApplication().invokeAndWait {
            if (shRunner.isAvailable(myProject)) {
                val output = runShellCommand(virtualFile)
                future.complete(output)
            }
        }

        return future.get()
    }

    private const val DEFAULT_TIMEOUT: Int = 30000

    private fun runShellCommand(virtualFile: VirtualFile): String {
        val workingDirectory = virtualFile.parent.path

        val commandLine: GeneralCommandLine = GeneralCommandLine()
            .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
            .withWorkDirectory(workingDirectory)
            .withCharset(StandardCharsets.UTF_8)
            .withExePath("sh")
            .withParameters(virtualFile.path)

        val processOutput = CapturingProcessHandler(commandLine).runProcess(DEFAULT_TIMEOUT)
        val exitCode = processOutput.exitCode
        if (exitCode != 0) {
            throw RuntimeException("Cannot execute ${commandLine}: exit code $exitCode, error output: ${processOutput.stderr}")
        }

        return processOutput.stdout
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
