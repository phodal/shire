package com.phodal.shirecore.middleware.builtin

import com.intellij.diff.DiffContentFactoryEx
import com.intellij.diff.DiffDialogHints
import com.intellij.diff.DiffManager
import com.intellij.diff.chains.SimpleDiffRequestChain
import com.intellij.diff.chains.SimpleDiffRequestProducer
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.util.ui.UIUtil
import com.phodal.shirecore.middleware.PostProcessorType
import com.phodal.shirecore.middleware.PostProcessorContext
import com.phodal.shirecore.middleware.PostProcessor

class DiffProcessor : PostProcessor {
    override val processorName: String = PostProcessorType.Diff.handleName
    private val diffFactory = DiffContentFactoryEx.getInstanceEx()

    override fun isApplicable(context: PostProcessorContext): Boolean {
        return true
    }

    override fun execute(
        project: Project,
        context: PostProcessorContext,
        console: ConsoleView?,
        args: List<Any>,
    ): Any {
        if (args.size < 2) {
            console?.print("DiffProcessor: not enough arguments", ConsoleViewContentType.ERROR_OUTPUT)
            return ""
        }

        val content1 = diffFactory.create(args[0].toString())
        val content2 = diffFactory.create(args[1].toString())


        val producer = SimpleDiffRequestProducer.create("Shire Diff") {
            SimpleDiffRequest("Shire Diff", content1, content2, "Current code", "Llm response")
        }

        val chain = SimpleDiffRequestChain.fromProducer(producer)
        UIUtil.invokeLaterIfNeeded { ->
            DiffManager.getInstance().showDiff(project, chain, DiffDialogHints.DEFAULT)
        }

        return ""
    }

}
