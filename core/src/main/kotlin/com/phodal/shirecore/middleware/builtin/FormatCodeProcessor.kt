package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.ShireRunVariableContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.workerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FormatCodeProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.FormatCode.handleName

    override fun isApplicable(context: ShireRunVariableContext): Boolean = true

    override fun execute(project: Project, context: ShireRunVariableContext, console: ConsoleView?, args: List<Any>): Any {
        val file = context.currentFile ?: return ""
        val document = PsiDocumentManager.getInstance(project).getDocument(file) ?: return ""

        CoroutineScope(workerThread).launch {
            WriteCommandAction.runWriteCommandAction(project) {
                val codeStyleManager = CodeStyleManager.getInstance(project)
                if (context.modifiedTextRange != null) {
                    codeStyleManager.reformatText(file, listOf(context.modifiedTextRange))
                } else if (context.genPsiElement != null) {
                    codeStyleManager.reformat(context.genPsiElement!!)
                } else {
                    codeStyleManager.reformatText(file, 0, document.textLength)
                }
            }
        }

        return context.genText ?: ""
    }
}
