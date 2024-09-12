package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.phodal.shirecore.middleware.PostProcessorType
import com.phodal.shirecore.middleware.ShireRunVariableContext
import com.phodal.shirecore.middleware.PostProcessor

class AppendProcessor : PostProcessor {
    override val processorName: String = PostProcessorType.Append.handleName

    override fun isApplicable(context: ShireRunVariableContext): Boolean = true

    override fun execute(
        project: Project,
        context: ShireRunVariableContext,
        console: ConsoleView?,
        args: List<Any>,
    ): Any {

        context.genText += args.map {
            if (it.toString().startsWith("$")) {
                context.compiledVariables[it.toString().substring(1)] ?: ""
            } else {
                it
            }
        }.joinToString(" ")

        return context.genText ?: ""
    }
}
