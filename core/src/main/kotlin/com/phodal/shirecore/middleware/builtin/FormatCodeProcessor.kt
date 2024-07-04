package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor

class FormatCodeProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.FormatCode.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean {
        return true
    }

    override fun execute(project: Project, context: PostCodeHandleContext, console: ConsoleView?): Any {
        val file = context.targetFile ?: return ""
        val document = PsiDocumentManager.getInstance(project).getDocument(file) ?: return ""

        if (context.modifiedTextRange != null) {
            return CodeStyleManager.getInstance(project).reformatText(file, listOf(context.modifiedTextRange))
        }

        return CodeStyleManager.getInstance(project).reformatText(file, 0, document.textLength)
    }
}
