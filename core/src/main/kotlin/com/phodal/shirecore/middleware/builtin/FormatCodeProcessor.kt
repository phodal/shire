package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor

class FormatCodeProcessor : PostProcessor {
    override val processorName: String get() = BuiltinPostHandler.FormatCode.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean {
        return true
    }

    override fun execute(project: Project, context: PostCodeHandleContext, console: ConsoleView?): Any {
        val file = context.targetFile ?: return ""

        val document = PsiDocumentManager.getInstance(project).getDocument(file)
        val text = document?.text ?: return ""

        val endOffset = document.textLength
        /// todo: change to modify range
        val formattedText = CodeStyleManager.getInstance(project).reformatText(file, 0, endOffset)

        return formattedText
    }
}
