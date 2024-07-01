package com.phodal.shirecore.interaction

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirecore.llm.ChatMessage
import com.phodal.shirecore.llm.ChatRole
import com.phodal.shirecore.provider.ide.LocationInteractionContext
import com.phodal.shirecore.provider.ide.LocationInteractionProvider
import com.phodal.shirecore.interaction.dto.CodeCompletionRequest
import com.phodal.shirecore.interaction.task.CodeCompletionTask
import com.phodal.shirecore.interaction.task.FileGenerateTask

class EditorInteractionProvider : LocationInteractionProvider {
    override fun isApplicable(context: LocationInteractionContext): Boolean {
        return true
    }

    override fun execute(context: LocationInteractionContext, postExecute: (String) -> Unit) {
        val msgs: List<ChatMessage> = listOf(
            // todo: add system prompt
            ChatMessage(ChatRole.User, context.prompt),
        )
        val targetFile = context.editor?.virtualFile

        when (context.interactionType) {
            InteractionType.AppendCursor,
            InteractionType.AppendCursorStream,
            -> {
                val task = createTask(context, msgs, isReplacement = false, postExecute = postExecute)

                if (task == null) {
                    ShirelangNotifications.error(context.project, "Failed to create code completion task.")
                    return
                }

                ProgressManager.getInstance()
                    .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
            }

            InteractionType.OutputFile -> {
                val fileName = targetFile?.name
                val task = FileGenerateTask(context.project, msgs, fileName, postExecute = postExecute)
                ProgressManager.getInstance()
                    .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
            }

            InteractionType.ReplaceSelection -> {
                val task = createTask(context, msgs, true, postExecute)

                if (task == null) {
                    ShirelangNotifications.error(context.project, "Failed to create code completion task.")
                    return
                }

                ProgressManager.getInstance()
                    .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
            }

            InteractionType.ReplaceCurrentFile -> {
                val fileName = targetFile?.name
                val task = FileGenerateTask(context.project, msgs, fileName, postExecute = postExecute)

                ProgressManager.getInstance()
                    .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
            }

            InteractionType.InsertBeforeSelection -> {
                TODO()
            }
        }
    }

    private fun createTask(
        context: LocationInteractionContext,
        msgs: List<ChatMessage>,
        isReplacement: Boolean,
        postExecute: (String) -> Unit,
    ): CodeCompletionTask? {
        if (context.editor == null) {
            ShirelangNotifications.error(context.project, "Editor is null, please open a file to continue.")
            return null
        }

        val editor = context.editor

        val offset = editor.caretModel.offset
        val userPrompt = msgs.filter { it.role == ChatRole.User }.joinToString("\n") { it.content }

        val request = runReadAction {
            CodeCompletionRequest.create(
                editor,
                offset,
                element = null,
                null,
                userPrompt,
                isReplacement = isReplacement,
                postExecute = postExecute
            )
        } ?: return null

        val task = CodeCompletionTask(request)
        return task
    }
}