package com.phodal.shirecore.config.interaction.task


import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.config.interaction.dto.CodeCompletionRequest
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.utils.markdown.CodeFence
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

class CodeCompletionTask(private val request: CodeCompletionRequest) :
    ShireInteractionTask(request.project, ShireCoreBundle.message("intentions.chat.code.complete.name"), request.postExecute) {
    private val logger = logger<CodeCompletionTask>()
    private var isCanceled: Boolean = false

    override fun run(indicator: ProgressIndicator) {
        val prompt = completionPrompt()

        val flow: Flow<String> = LlmProvider.provider(request.project)!!.stream(prompt, "", false)
        logger.info("Prompt: $prompt")

        val editor = request.editor
        ShireCoroutineScope.scope(request.project).launch {
            var currentOffset = request.startOffset

            val project = request.project
            val suggestion = StringBuilder()

            flow.cancellable().collect { char ->
                if (isCanceled) {
                    cancel()
                    return@collect
                }

                val parsedContent = CodeFence.parse(char).text;

                suggestion.append(parsedContent)
                invokeLater {
                    if (!isCanceled && !request.isReplacement) {
                        InsertUtil.insertStreamingToDoc(project, parsedContent, editor, currentOffset)
                        currentOffset += char.length
                    }
                }
            }

            if (request.isReplacement) {
                // remove all selection code
                val selectionModel = editor.selectionModel
                val start = selectionModel.selectionStart
                val end = selectionModel.selectionEnd
                editor.document.deleteString(start, end)

                InsertUtil.insertStringAndSaveChange(
                    project,
                    suggestion.toString(),
                    editor.document,
                    request.startOffset,
                    false
                )
            }

            logger.info("Suggestion: $suggestion")
            request.postExecute.invoke(suggestion.toString(), null)

            indicator.fraction = 1.0
        }
    }

    private fun completionPrompt(): String {
        val documentLength = request.editor.document.textLength
        val prefix = if (request.startOffset > documentLength) {
            request.prefixText
        } else {
            val text = request.editor.document.text
            text.substring(0, request.startOffset)
        }

        val prompt = "complete code for given code: \n$prefix"

        return prompt
    }
}