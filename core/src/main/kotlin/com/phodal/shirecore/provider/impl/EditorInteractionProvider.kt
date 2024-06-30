package com.phodal.shirecore.provider.impl

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirecore.llm.ChatMessage
import com.phodal.shirecore.provider.ide.LocationInteractionContext
import com.phodal.shirecore.provider.ide.LocationInteractionProvider

class EditorInteractionProvider : LocationInteractionProvider {
    override fun isApplicable(context: LocationInteractionContext): Boolean {
        return true
    }

    override fun execute(context: LocationInteractionContext): String {
        val msgs: List<ChatMessage> = listOf()
        val targetFile = context.editor?.virtualFile
        val result: String = ""

        when (context.interactionType) {
            InteractionType.AppendCursor -> TODO()
            InteractionType.AppendCursorStream -> TODO()
            InteractionType.OutputFile -> {
                // todo: replace real file name
                val fileName = targetFile?.name
                val task = FileGenerateTask(context.project, msgs, fileName)
                ProgressManager.getInstance()
                    .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
            }
            InteractionType.ReplaceSelection -> {
            }
            InteractionType.ReplaceCurrentFile -> {
                // todo: replace real file name
                val fileName = targetFile?.name
                val task = FileGenerateTask(context.project, msgs, fileName)

                ProgressManager.getInstance()
                    .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
            }
            InteractionType.InsertBeforeSelection -> {
                TODO()
            }
        }

        return result
    }
}