package com.phodal.shirecore.interaction.task

import com.intellij.openapi.util.TextRange

import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.interaction.dto.CodeCompletionRequest
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.markdown.Code
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

open class BasicChatCompletionTask(private val request: CodeCompletionRequest) :
    Task.Backgroundable(request.project, ShireCoreBundle.message("intentions.chat.code.complete.name")) {
    private val logger = logger<BasicChatCompletionTask>()

    private var isCanceled: Boolean = false

    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = false
        indicator.fraction = 0.1
        indicator.text = ShireCoreBundle.message("intentions.step.prepare-context")

        val flow: Flow<String> = LlmProvider.provider(request.project)!!.stream(request.userPrompt, "", false)
        logger.info("Prompt: ${request.userPrompt}")

        DumbAwareAction.create {
            isCanceled = true
        }.registerCustomShortcutSet(
            CustomShortcutSet(
                KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), null),
            ),
            request.editor.component
        )

        val editor = request.editor
        val project = request.project

        var currentOffset = request.startOffset
        val modifyStart = request.startOffset

        indicator.isIndeterminate = false
        indicator.fraction = 0.5
        indicator.text = ShireCoreBundle.message("intentions.request.background.process.title")

        ShireCoroutineScope.scope(request.project).launch {
            val suggestion = StringBuilder()

            flow.cancellable().collect { char ->
                if (isCanceled) {
                    cancel()
                    return@collect
                }

                suggestion.append(char)

                invokeLater {
                    if (!isCanceled && !request.isReplacement) {
                        if (request.isInsertBefore) {
                            InsertUtil.insertStreamingToDoc(project, char, editor, currentOffset)
                            currentOffset += char.length
                        } else {
                            InsertUtil.insertStreamingToDoc(project, char, editor, currentOffset)
                            currentOffset += char.length
                        }
                    }
                }
            }

            val modifyEnd = currentOffset

            if (request.isReplacement) {
                val parsedContent = Code.parse(suggestion.toString()).text
                InsertUtil.replaceText(project, editor, parsedContent)
            }

            indicator.fraction = 0.8
            logger.info("Suggestion: $suggestion")

            val textRange: TextRange = TextRange(modifyStart, modifyEnd)

            request.postExecute.invoke(suggestion.toString(), textRange)
            indicator.fraction = 1.0
        }
    }

    override fun onThrowable(error: Throwable) {
        super.onThrowable(error)
    }

    override fun onCancel() {
        this.isCanceled = true
        super.onCancel()
    }
}
