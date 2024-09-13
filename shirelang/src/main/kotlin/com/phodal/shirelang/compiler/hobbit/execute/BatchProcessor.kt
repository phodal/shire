package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.project.Project

class BatchProcessor {
    companion object {
        fun execute(
            myProject: Project,
            fileName: String,
            inputs: List<String>,
            batchSize: Int,
            variableTable: MutableMap<String, Any?>,
        ): Any {
            return ""
        }
    }
}
