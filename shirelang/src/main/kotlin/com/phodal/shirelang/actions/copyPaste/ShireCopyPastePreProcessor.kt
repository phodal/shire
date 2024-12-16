package com.phodal.shirelang.actions.copyPaste

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RawText
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.utils.markdown.CodeFence
import com.phodal.shirecore.middleware.post.PostProcessorContext
import com.phodal.shirelang.compiler.template.ShireTemplateCompiler
import com.phodal.shirelang.compiler.ast.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.precompile.preAnalysisSyntax
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

@Service(Service.Level.APP)
class PasteManagerService {
    private val pasteProcessorMap = mutableMapOf<HobbitHole, ShireFile>()

    fun registerPasteProcessor(key: HobbitHole, file: ShireFile) {
        pasteProcessorMap[key] = file
    }

    fun firstProcessor(): HobbitHole? {
        return pasteProcessorMap.keys.firstOrNull()
    }

    fun executeProcessor(project: Project, hobbitHole: HobbitHole, text: String, file: PsiFile, editor: Editor): String {
        val future = CompletableFuture<String>()
        val shireFile = pasteProcessorMap[hobbitHole] ?: return text

        val compileResult = preAnalysisSyntax(shireFile, project)
        val variableTable = compileResult.variableTable

        val templateCompiler =
            ShireTemplateCompiler(project, hobbitHole, variableTable, compileResult.shireOutput, editor)
        templateCompiler.putCustomVariable("text", text)

        val promptText = runBlocking {
            templateCompiler.compile().trim()
        }

        PostProcessorContext.getData()?.lastTaskOutput?.let {
            templateCompiler.putCustomVariable("output", it)
        }

        val flow: Flow<String>? = LlmProvider.provider(project)?.stream(promptText, "", false)
        ShireCoroutineScope.scope(project).launch {
            val suggestion = StringBuilder()

            flow?.cancellable()?.collect { char ->
                suggestion.append(char)
            }

            val code = CodeFence.parse(suggestion.toString())
            future.complete(code.text)

            logger<ShireCopyPastePreProcessor>().info("paste code: $code")
        }

        return future.get()
    }

    companion object {
        fun getInstance(): PasteManagerService =
            ApplicationManager.getApplication().getService(PasteManagerService::class.java)
    }
}

class ShireCopyPastePreProcessor : CopyPastePreProcessor {
    override fun preprocessOnCopy(file: PsiFile, startOffsets: IntArray, endOffsets: IntArray, text: String): String? {
        return text
    }

    override fun preprocessOnPaste(
        project: Project,
        file: PsiFile,
        editor: Editor,
        text: String,
        rawText: RawText?,
    ): String {
        val instance = PasteManagerService.getInstance()
        val hobbitHole = instance.firstProcessor() ?: return text
        if (!hobbitHole.enabled) return text

        /// only for test java and kotlin
        val language = file.language.displayName.lowercase()
        if (!(language == "java" || language == "kotlin")) {
            return text
        }

        /// should be more than 7 lines
        if (text.lines().size < 5) {
            return text
        }

        return instance.executeProcessor(project, hobbitHole, text, file, editor)
    }
}
