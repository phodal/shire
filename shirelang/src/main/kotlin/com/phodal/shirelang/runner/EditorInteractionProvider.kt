package com.phodal.shirelang.runner

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.phodal.shire.llm.model.ChatMessage
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirelang.run.runner.tasks.FileGenerateTask

class EditorInteractionProvider : LocationInteractionProvider {
    override fun isApplicable(context: LocationInteractionContext): Boolean {
        TODO("Not yet implemented")
    }

    override fun execute(context: LocationInteractionContext) {
        val msgs: List<ChatMessage> = listOf()
        val targetFile = context.editor?.virtualFile

        when (context.hole?.interaction) {
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

            null -> TODO()
        }

    }
}