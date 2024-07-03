package com.phodal.shirecore.interaction

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirecore.interaction.dto.CodeCompletionRequest
import com.phodal.shirecore.interaction.task.BaseCodeGenTask
import com.phodal.shirecore.interaction.task.FileGenerateTask
import com.phodal.shirecore.llm.ChatMessage
import com.phodal.shirecore.llm.ChatRole
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.provider.ide.LocationInteractionContext
import com.phodal.shirecore.provider.ide.LocationInteractionProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

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

            InteractionType.RunPanel -> {
                val flow: Flow<String> = LlmProvider.provider(context.project)!!.stream(context.prompt, "", false)
                ShireCoroutineScope.scope(context.project).launch {
                    val suggestion = StringBuilder()

                    flow.cancellable().collect { char ->
                        suggestion.append(char)

                        invokeLater {
                            context.console.print(char, ConsoleViewContentType.NORMAL_OUTPUT)
                        }
                    }

                    postExecute.invoke(suggestion.toString())
                }
            }
        }
    }

    private fun createTask(
        context: LocationInteractionContext,
        msgs: List<ChatMessage>,
        isReplacement: Boolean,
        postExecute: (String) -> Unit,
    ): BaseCodeGenTask? {
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

        val task = BaseCodeGenTask(request)
        return task
    }
}