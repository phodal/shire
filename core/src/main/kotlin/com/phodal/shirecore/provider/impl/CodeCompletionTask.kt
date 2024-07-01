package com.phodal.shirecore.provider.impl


import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.provider.impl.dto.CodeCompletionRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.jvm.internal.Ref

class CodeCompletionTask(private val request: CodeCompletionRequest) :
    Task.Backgroundable(request.project, ShireCoreBundle.message("intentions.chat.code.complete.name")) {


    private val writeActionGroupId = "code.complete.intention.write.action"
    private val codeMessage = ShireCoreBundle.message("intentions.chat.code.complete.name")

//    private val chunksString = request.element?.let { SimilarChunksWithPaths.createQuery(it, 60) }
//    private val commenter = request.element?.let { LanguageCommenters.INSTANCE.forLanguage(it.language) }
//    private val commentPrefix = commenter?.lineCommentPrefix

    override fun run(indicator: ProgressIndicator) {
        val prompt = promptText()

        val flow: Flow<String> = LlmProvider.provider(request.project)!!.stream(prompt, "", false)
        logger.info("Prompt: $prompt")

        val editor = request.editor
        ShireCoroutineScope.scope(request.project).launch {
            val currentOffset = Ref.IntRef()
            currentOffset.element = request.offset

            val project = request.project
            val finalOutput = StringBuilder()

            flow.collect {
                finalOutput.append(it)
                invokeLater {
                    WriteCommandAction.runWriteCommandAction(project, codeMessage, writeActionGroupId, {
                        insertStringAndSaveChange(project, it, editor.document, currentOffset.element, false)
                    })

                    currentOffset.element += it.length
                    editor.caretModel.moveToOffset(currentOffset.element)
                    editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
                }
            }

            logger.info("Suggestion: $finalOutput")
            request.postExecute?.invoke(finalOutput.toString())
        }
    }

    private fun promptText(): String {
        val documentLength = request.editor.document.textLength
        val prefix = if (request.offset > documentLength) {
            request.prefixText
        } else {
            val text = request.editor.document.text
            text.substring(0, request.offset)
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
            withReformat: Boolean
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