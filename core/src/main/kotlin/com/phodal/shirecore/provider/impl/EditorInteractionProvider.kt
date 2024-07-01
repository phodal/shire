package com.phodal.shirecore.provider.impl

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirecore.llm.ChatMessage
import com.phodal.shirecore.llm.ChatRole
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import com.phodal.shirecore.provider.ide.LocationInteractionContext
import com.phodal.shirecore.provider.ide.LocationInteractionProvider
import com.phodal.shirecore.provider.impl.dto.CodeCompletionRequest

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
                val fileName = targetFile?.name
                val task = FileGenerateTask(context.project, msgs, fileName)
                ProgressManager.getInstance()
                    .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
            }

            InteractionType.ReplaceSelection -> {
                if (context.editor == null) {
                    ShirelangNotifications.error(context.project, "Editor is null, please open a file to continue.")
                    return ""
                }

                val task = createTask(context.editor, context, msgs)

                if (task == null) {
                    ShirelangNotifications.error(context.project, "Failed to create code completion task.")
                    return ""
                }

                ProgressManager.getInstance()
                    .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
            }

            InteractionType.ReplaceCurrentFile -> {
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

    private fun createTask(
        editor: Editor,
        context: LocationInteractionContext,
        msgs: List<ChatMessage>,
    ): CodeCompletionTask? {
        val offset = editor.caretModel.offset
        val element = SelectElementStrategy.resolvePsiElement(context.project, editor)
        val userPrompt = msgs.filter { it.role == ChatRole.User }.joinToString("\n") { it.content }

        val request = runReadAction {
            CodeCompletionRequest.create(editor, offset, element, null, userPrompt, isReplacement = true)
        } ?: return null

        val task = CodeCompletionTask(request)
        return task
    }
}