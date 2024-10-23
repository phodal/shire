package com.phodal.shirelang.compiler.execute.processor

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shirelang.ShireActionStartupActivity
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.compiler.execute.FunctionStatementProcessor
import com.phodal.shirelang.compiler.ast.patternaction.PatternActionFuncDef
import com.phodal.shirelang.compiler.ast.patternaction.PatternProcessor

object BatchProcessor : PatternProcessor {
    override val type: PatternActionFuncDef = PatternActionFuncDef.BATCH
    fun execute(
        myProject: Project,
        filename: String,
        inputs: List<String>,
        batchSize: Int,
        variableTable: MutableMap<String, Any?>,
    ): Any {
        val file = runReadAction {
            ShireActionStartupActivity.findShireFile(myProject, filename)
        }

        if (file == null) {
            logger<FunctionStatementProcessor>().error("execute shire error: file not found")
            return ""
        }

        var files = inputs
        /// maybe inputs ["a.txt\nb.txt", "c.txt\nd.txt"] or ["a.txt", "b.txt", "c.txt", "d.txt"] we need to split it
        if (inputs.size == 1) {
            files = inputs[0].split("\n")
        }


        return files.forEach { chunk: String ->
            try {
                val variableNames = arrayOf("input")
                variableTable["input"] = chunk
                ShireRunFileAction.suspendExecuteFile(myProject, variableNames, variableTable, file) ?: ""
            } catch (e: Exception) {
                logger<FunctionStatementProcessor>().error("execute shire error: $e")
            }
        }
    }
}
