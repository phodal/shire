package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shirelang.ShireActionStartupActivity
import com.phodal.shirelang.actions.ShireRunFileAction

object ExecuteProcessor {
    private val logger = logger<ExecuteProcessor>()

    fun execute(
        myProject: Project,
        filename: Any,
        variableNames: Array<String>,
        variableTable: MutableMap<String, Any?>,
    ): Any {
        return executeShireFile(myProject, filename, variableNames, variableTable)
    }

    private fun executeShireFile(
        myProject: Project,
        filename: Any,
        variableNames: Array<String>,
        variableTable: MutableMap<String, Any?>,
    ): String {
        try {
            val file = runReadAction {
                ShireActionStartupActivity.obtainShireFiles(myProject).find {
                    it.name == filename
                }
            }

            if (file == null) {
                logger.warn("execute shire error: file not found")
                return ""
            }

            return ShireRunFileAction.suspendExecuteFile(myProject, variableNames, variableTable, file) ?: ""
        } catch (e: Exception) {
            logger.warn("execute shire error: $e")
            return ""
        }
    }

}
