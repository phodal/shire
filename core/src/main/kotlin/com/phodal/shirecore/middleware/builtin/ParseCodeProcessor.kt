package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.phodal.shirecore.markdown.CodeFence
import com.phodal.shirecore.middleware.PostProcessorType
import com.phodal.shirecore.middleware.ShireRunVariableContext
import com.phodal.shirecore.middleware.PostProcessor

class ParseCodeProcessor : PostProcessor {
    override val processorName: String = PostProcessorType.ParseCode.handleName

    override fun isApplicable(context: ShireRunVariableContext): Boolean = true

    override fun execute(project: Project, context: ShireRunVariableContext, console: ConsoleView?, args: List<Any>): String {
        val code = CodeFence.parse(context.genText ?: "")
        val codeText = code.text

        context.genTargetLanguage = code.ideaLanguage
        context.genTargetExtension = code.extension

        context.pipeData["output"] = codeText
        context.pipeData["code"] = codeText

        return codeText
    }
}
