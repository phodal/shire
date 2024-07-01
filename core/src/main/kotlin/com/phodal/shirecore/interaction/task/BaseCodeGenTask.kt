package com.phodal.shirecore.interaction.task

import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.util.TextRange
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.interaction.dto.CodeCompletionRequest
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.markdown.Code
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

open class BaseCodeGenTask(private val request: CodeCompletionRequest) :
    Task.Backgroundable(request.project, ShireCoreBundle.message("intentions.chat.code.complete.name")) {
    private val logger = logger<BaseCodeGenTask>()

    private var isCanceled: Boolean = false
    open fun promptText(): String {
        val selectionModel = request.editor.selectionModel
        if (selectionModel.hasSelection()) {
            return runReadAction {
                val start = selectionModel.selectionStart
                val end = selectionModel.selectionEnd
                val selectedText = request.editor.document.getText(TextRange(start, end))
                return@runReadAction selectedText
            }
        }

        val element = SelectElementStrategy.resolvePsiElement(request.project, request.editor)
        if (element != null) {
            return element.text
        }

        return ""
    }

    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = false
        indicator.fraction = 0.1
        indicator.text = ShireCoreBundle.message("intentions.step.prepare-context")

        val prompt = promptText()

        val flow: Flow<String> = LlmProvider.provider(request.project)!!.stream(prompt, "", false)
        logger.info("Prompt: $prompt")

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
                        InsertUtil.insertStreamingToDoc(project, char, editor, currentOffset)
                        currentOffset += char.length
                    }
                }
            }

            if (request.isReplacement) {
                val parsedContent = Code.parse(suggestion.toString()).text
                InsertUtil.replaceText(project, editor, parsedContent)
            }

            indicator.fraction = 0.8
            logger.info("Suggestion: $suggestion")

            request.postExecute?.invoke(suggestion.toString())
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
