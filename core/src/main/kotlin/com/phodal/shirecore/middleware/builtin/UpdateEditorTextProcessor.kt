package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.workerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class UpdateEditorTextProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.UpdateEditorText.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean = true

    override fun execute(
        project: Project,
        context: PostCodeHandleContext,
        console: ConsoleView?,
        args: List<Any>,
    ): Any {
        val editor = context.editor ?: return ""
        val newText = if(args.isNotEmpty()) {
            args[0]
        } else {
            context.pipeData["output"]
        }

        if (newText == null) {
            logger<UpdateEditorTextProcessor>().error("no new code to update, pipeData: ${context.pipeData}")
            return ""
        }

        CoroutineScope(workerThread).launch {
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.setText(newText.toString())
            }
        }

        return newText
    }
}
