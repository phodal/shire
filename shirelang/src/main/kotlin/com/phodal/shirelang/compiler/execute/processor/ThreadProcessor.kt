package com.phodal.shirelang.compiler.execute.processor

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.readText
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.sh.psi.ShFile
import com.intellij.sh.run.ShRunner
import com.phodal.shirecore.lookupFile
import com.phodal.shirecore.provider.http.HttpHandler
import com.phodal.shirecore.provider.http.HttpHandlerType
import com.phodal.shirecore.provider.shire.FileRunService
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.compiler.parser.SHIRE_ERROR
import com.phodal.shirelang.compiler.execute.processor.shell.ShireShellCommandRunner
import com.phodal.shirelang.compiler.ast.patternaction.PatternActionFuncDef
import com.phodal.shirelang.compiler.ast.patternaction.PatternProcessor
import com.phodal.shirelang.psi.ShireFile
import java.util.concurrent.CompletableFuture


object ThreadProcessor: PatternProcessor {
    override val type: PatternActionFuncDef = PatternActionFuncDef.THREAD

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
                            executeTask(myProject, variablesName, variableTable, psiFile)
                                ?: "$SHIRE_ERROR - Thread: No run service found"
                        }
                    }

                    else -> {
                        return executeTask(myProject, variablesName, variableTable, psiFile)
                            ?: "$SHIRE_ERROR - Thread: No run service found"
                    }
                }
            }

            is ShFile -> {
                val processVariables: Map<String, String> =
                    variablesName.associateWith { (variableTable[it] as? String ?: "") }
                return executeShFile(psiFile, myProject, processVariables)
            }

            else -> return FileRunService.provider(myProject, file)?.runFile(myProject, file, psiFile)
                ?: "No run service found for $psiFile, $fileName"
        }
    }

    private fun executeShFile(psiFile: ShFile, myProject: Project, processVariables: Map<String, String>): String {
        val virtualFile = psiFile.virtualFile
        val shRunner = ApplicationManager.getApplication().getService(ShRunner::class.java)
            ?: return "$SHIRE_ERROR: Shell runner not found"

        val future = CompletableFuture<String>()
        ApplicationManager.getApplication().invokeLater {
            if (shRunner.isAvailable(myProject)) {
                try {
                    val output = ShireShellCommandRunner.runShellCommand(virtualFile, myProject, processVariables)
                    future.complete(output)
                } catch (t: Throwable) {
                    future.completeExceptionally(t)
                }
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

