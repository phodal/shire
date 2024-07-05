package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.phodal.shirecore.markdown.Code
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor

class ParseCodeProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.ParseCode.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean {
        return true
    }

    /**
     * Todo: support parse [PostCodeHandleContext.currentParams] for language in parse
     */
    override fun execute(project: Project, context: PostCodeHandleContext, console: ConsoleView?, args: List<Any>): String {
        val code = Code.parse(context.genText ?: "")
        val codeText = code.text

        context.genTargetLanguage = code.ideaLanguage
        context.genTargetExtension = code.extension

        context.pipeData["output"] = codeText
        context.pipeData["code"] = codeText

        return codeText
    }
}
