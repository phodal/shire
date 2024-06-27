package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor

class RunCodeProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.RunCode.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean {
        return true
    }

    override fun execute(project: Project, context: PostCodeHandleContext, console: ConsoleView?): String {
//        return context.currentFile?.virtualFile?.let {
//            FileRunService.provider(project, it)?.runFile(project, it, context.currentElement)
//        } ?: ""

        return ""
    }
}
