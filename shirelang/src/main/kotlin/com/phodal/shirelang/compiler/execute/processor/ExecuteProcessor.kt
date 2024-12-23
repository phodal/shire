package com.phodal.shirelang.compiler.execute.processor

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shirecore.lookupFile
import com.phodal.shirecore.provider.shire.FileRunService
import com.phodal.shirecore.workerThread
import com.phodal.shirelang.ShireActionStartupActivity
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.compiler.parser.SHIRE_ERROR
import com.phodal.shirelang.compiler.execute.command.RunShireCommand
import com.phodal.shirelang.compiler.ast.patternaction.PatternActionFuncDef
import com.phodal.shirelang.compiler.ast.patternaction.PatternProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object ExecuteProcessor : PatternProcessor {
    override val type: PatternActionFuncDef = PatternActionFuncDef.EXECUTE

    private val logger = logger<ExecuteProcessor>()

    fun execute(
        myProject: Project,
        filename: Any,
        variableNames: Array<String>,
        variableTable: MutableMap<String, Any?>,
    ): Any {
        val file = filename.toString()
        if (file.endsWith(".shire")) {
            return executeShireFile(myProject, filename, variableNames, variableTable)
        }

        if (file.startsWith(":")) {
            CoroutineScope(workerThread).launch {
                RunShireCommand(myProject, file).doExecute()
            }
        }

        val virtualFile = myProject.lookupFile(file) ?: return "$SHIRE_ERROR: File not found: $filename"

        val runService = FileRunService.provider(myProject, virtualFile)
        return runService?.runFileAsync(myProject, virtualFile, null)
            ?: "$SHIRE_ERROR: [ExecuteProcessor] No run service found for file: $filename"
    }

    private fun executeShireFile(
        myProject: Project,
        filename: Any,
        variableNames: Array<String>,
        variableTable: MutableMap<String, Any?>,
    ): String {
        try {
            val file = runReadAction {
                ShireActionStartupActivity.findShireFile(myProject, filename.toString())
            }

            if (file == null) {
                logger.warn("execute shire error: file not found")
                return ""
            }

            return ShireRunFileAction.suspendExecuteFile(myProject, file, variableNames, variableTable) ?: ""
        } catch (e: Exception) {
            logger.warn("execute shire error: $e")
            return ""
        }
    }

}
