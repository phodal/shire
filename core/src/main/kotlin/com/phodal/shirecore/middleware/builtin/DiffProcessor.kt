package com.phodal.shirecore.middleware.builtin

import com.intellij.diff.DiffContentFactoryEx
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.project.Project
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor

class DiffProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.Diff.handleName
    private val diffFactory = DiffContentFactoryEx.getInstanceEx()

    override fun isApplicable(context: PostCodeHandleContext): Boolean {
        return true
    }

    override fun execute(
        project: Project,
        context: PostCodeHandleContext,
        console: ConsoleView?,
        args: List<Any>,
    ): Any {
        if (args.size < 2) {
            console?.print("DiffProcessor: not enough arguments", ConsoleViewContentType.ERROR_OUTPUT)
            return ""
        }

        val content1 = diffFactory.create(args[0].toString())
        val content2 = diffFactory.create(args[1].toString())

        val simpleDiffRequest = SimpleDiffRequest("Shire Diff", content1, content2, "Current code", "Llm response")
        runInEdt {
            DiffManager.getInstance().showDiff(project, simpleDiffRequest)
        }

        return ""
    }

}
