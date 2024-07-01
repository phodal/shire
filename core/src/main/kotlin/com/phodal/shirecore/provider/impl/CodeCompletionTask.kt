package com.phodal.shirecore.provider.impl


import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.markdown.Code
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import com.phodal.shirecore.provider.impl.dto.CodeCompletionRequest
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

class CodeCompletionTask(private val request: CodeCompletionRequest) :
    Task.Backgroundable(request.project, ShireCoreBundle.message("intentions.chat.code.complete.name")) {


    private val writeActionGroupId = "code.complete.intention.write.action"
    private val codeMessage = ShireCoreBundle.message("intentions.chat.code.complete.name")

    //    private val chunksString = request.element?.let { SimilarChunksWithPaths.createQuery(it, 60) }
//    private val commenter = request.element?.let { LanguageCommenters.INSTANCE.forLanguage(it.language) }
//    private val commentPrefix = commenter?.lineCommentPrefix
    private var isCanceled: Boolean = false

    override fun run(indicator: ProgressIndicator) {
        val prompt = promptText(request.editor)

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

                val parsedContent = Code.parse(char).text;

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

                InsertUtil.insertStringAndSaveChange(project, suggestion.toString(), editor.document, request.startOffset, false)
            }

            logger.info("Suggestion: $suggestion")
            request.postExecute?.invoke(suggestion.toString())
        }
    }

    private fun promptText(editor: Editor): String {
        val selectionModel = editor.selectionModel
        if (selectionModel.hasSelection()) {
            val start = selectionModel.selectionStart
            val end = selectionModel.selectionEnd
            val selectedText = editor.document.getText(TextRange(start, end))
            return selectedText
        }

        val element = SelectElementStrategy.resolvePsiElement(request.project, editor)
        if (element != null) {
            return element.text
        }


        return completionPrompt()
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


    companion object {
        private val logger = logger<CodeCompletionTask>()

        fun insertStringAndSaveChange(
            project: Project,
            suggestion: String,
            document: Document,
            startOffset: Int,
            withReformat: Boolean,
        ) {
            document.insertString(startOffset, suggestion)
            PsiDocumentManager.getInstance(project).commitDocument(document)

            if (!withReformat) return

            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)
            psiFile?.let { file ->
                val reformatRange = TextRange(startOffset, startOffset + suggestion.length)
                CodeStyleManager.getInstance(project).reformatText(file, listOf(reformatRange))
            }
        }
    }
}