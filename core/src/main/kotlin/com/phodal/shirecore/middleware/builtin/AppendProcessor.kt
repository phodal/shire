package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor

class AppendProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.Append.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean {
        return true
    }

    override fun execute(
        project: Project,
        context: PostCodeHandleContext,
        console: ConsoleView?,
        args: List<Any>,
    ): Any {
        context.genText += args.joinToString("")

        return context.genText ?: ""
    }
}
