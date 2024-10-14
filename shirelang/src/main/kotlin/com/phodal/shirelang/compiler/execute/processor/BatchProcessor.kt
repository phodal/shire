package com.phodal.shirelang.compiler.execute.processor

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shirelang.ShireActionStartupActivity
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.compiler.execute.FunctionStatementProcessor
import com.phodal.shirelang.compiler.ast.patternaction.PatternActionFuncDef
import com.phodal.shirelang.compiler.ast.patternaction.PatternProcessor

class BatchProcessor : PatternProcessor {
    override val type: PatternActionFuncDef = PatternActionFuncDef.BATCH

    companion object {
        fun execute(
            myProject: Project,
            filename: String,
            inputs: List<String>,
            batchSize: Int,
            variableTable: MutableMap<String, Any?>,
        ): Any {
            val file = runReadAction {
                ShireActionStartupActivity.findShireFile(myProject, filename.toString())
            }

            if (file == null) {
                logger<FunctionStatementProcessor>().warn("execute shire error: file not found")
                return ""
            }

            return inputs.forEach { chunk: String ->
                try {
                    val variableNames = arrayOf("input")
                    variableTable["input"] = chunk
                    ShireRunFileAction.suspendExecuteFile(myProject, variableNames, variableTable, file) ?: ""
                } catch (e: Exception) {
                    logger<FunctionStatementProcessor>().warn("execute shire error: $e")
                }
            }
        }
    }
}
