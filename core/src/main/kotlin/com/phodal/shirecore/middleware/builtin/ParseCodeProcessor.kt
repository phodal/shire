package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.phodal.shirecore.markdown.CodeFence
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.ShireRunContext
import com.phodal.shirecore.middleware.PostProcessor

class ParseCodeProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.ParseCode.handleName

    override fun isApplicable(context: ShireRunContext): Boolean = true

    override fun execute(project: Project, context: ShireRunContext, console: ConsoleView?, args: List<Any>): String {
        val code = CodeFence.parse(context.genText ?: "")
        val codeText = code.text

        context.genTargetLanguage = code.ideaLanguage
        context.genTargetExtension = code.extension

        context.pipeData["output"] = codeText
        context.pipeData["code"] = codeText

        return codeText
    }
}
