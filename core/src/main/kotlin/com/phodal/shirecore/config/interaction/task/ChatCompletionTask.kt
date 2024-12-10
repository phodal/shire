package com.phodal.shirecore.config.interaction.task

import com.intellij.openapi.util.TextRange

import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.config.interaction.dto.CodeCompletionRequest
import com.phodal.shirecore.runner.console.cancelHandler
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.utils.markdown.CodeFence
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.awt.event.KeyEvent
import javax.swing.KeyStroke
import com.intellij.diff.tools.simple.SimpleDiffViewer
import com.intellij.diff.tools.util.TwosideContentPanel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.components.JBPanel
import java.awt.BorderLayout
import javax.swing.JPanel

open class ChatCompletionTask(private val request: CodeCompletionRequest) :
    ShireInteractionTask(request.project, ShireCoreBundle.message("intentions.chat.code.complete.name"), request.postExecute) {
    private val logger = logger<ChatCompletionTask>()

    private var isCanceled: Boolean = false

    private var cancelCallback: ((String) -> Unit)? = null

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

            flow.cancelHandler { cancelCallback = it }.cancellable().collect { char ->
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
                val parsedContent = CodeFence.parse(suggestion.toString()).text
                InsertUtil.replaceText(project, editor, parsedContent)
            }

            indicator.fraction = 0.8
            logger.info("Suggestion: $suggestion")

            val textRange = TextRange(modifyStart, modifyEnd)

            request.postExecute.invoke(suggestion.toString(), textRange)
            indicator.fraction = 1.0
        }
    }

    override fun onThrowable(error: Throwable) {
        super.onThrowable(error)
        if (error.message?.contains("/goto") == true) {
            handleGotoCommand(error.message!!)
        }
    }

    private fun handleGotoCommand(command: String) {
        val symbol = command.substringAfter("/goto").trim()
        val file = findFileContainingSymbol(symbol)
        if (file != null) {
            openFileInEditor(file)
        } else {
            logger.warn("Symbol not found: $symbol")
        }
    }

    private fun findFileContainingSymbol(symbol: String): VirtualFile? {
        // Implement logic to find the file containing the symbol
        return null
    }

    private fun openFileInEditor(file: VirtualFile) {
        invokeLater {
            FileEditorManager.getInstance(request.project).openFile(file, true)
        }
    }

    override fun onCancel() {
        this.isCanceled = true
        this.cancelCallback?.invoke("This job is canceled")
        super.onCancel()
    }

    private fun createDiffViewerPanel(): JPanel {
        val panel = JBPanel<JBPanel<*>>(BorderLayout())
        val contentPanel = TwosideContentPanel()
        val diffViewer = SimpleDiffViewer(request.project, contentPanel)
        panel.add(diffViewer.component, BorderLayout.CENTER)
        return panel
    }

    private fun addHyperlinkToCompileOutput(output: String): String {
        val hyperlinkRegex = Regex("(https?://\\S+)")
        return output.replace(hyperlinkRegex) { matchResult ->
            "<a href=\"${matchResult.value}\">${matchResult.value}</a>"
        }
    }
}
