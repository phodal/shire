package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.workerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class InsertNewlineProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.InsertNewline.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean = true

    override fun execute(project: Project, context: PostCodeHandleContext, console: ConsoleView?, args: List<Any>): Any {
        val editor = context.editor ?: return ""

        CoroutineScope(workerThread).launch {
            WriteCommandAction.runWriteCommandAction(project) {
                // insert \n at cursor position
                editor.document.insertString(editor.caretModel.offset, "\n")
            }
        }

        return editor.document.text
    }
}
