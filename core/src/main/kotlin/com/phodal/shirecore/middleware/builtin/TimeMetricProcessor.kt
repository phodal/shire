package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.phodal.shirecore.middleware.PostProcessorType
import com.phodal.shirecore.middleware.ShireRunVariableContext
import com.phodal.shirecore.middleware.PostProcessor

class TimeMetricProcessor : PostProcessor {
    private var startTime: Long? = null

    override val processorName: String = PostProcessorType.TimeMetric.handleName

    override fun isApplicable(context: ShireRunVariableContext): Boolean = true

    override fun setup(context: ShireRunVariableContext): String {
        startTime = System.currentTimeMillis()
        return startTime.toString()
    }

    override fun execute(project: Project, context: ShireRunVariableContext, console: ConsoleView?, args: List<Any>): String {
        val endTime = System.currentTimeMillis()
        return (endTime - startTime!!).toString()
    }
}
