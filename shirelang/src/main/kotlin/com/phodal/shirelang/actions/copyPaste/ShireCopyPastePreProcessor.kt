package com.phodal.shirelang.actions.copyPaste

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RawText
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.config.interaction.PostFunction
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.provider.ide.LocationInteractionContext
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

@Service(Service.Level.APP)
class PasteManagerService {
    private val pasteProcessorMap = mutableMapOf<HobbitHole, PasteProcessorConfig>()

    fun registerPasteProcessor(key: HobbitHole, config: PasteProcessorConfig) {
        pasteProcessorMap[key] = config
    }

    fun firstProcessor(): HobbitHole? {
        return pasteProcessorMap.keys.firstOrNull()
    }

    fun executeProcessor(project: Project, hole: HobbitHole, text: String): String {
        val future = CompletableFuture<String>()
        val config = pasteProcessorMap[hole] ?: return text

        val flow: Flow<String>? = LlmProvider.provider(project)?.stream(config.context.prompt, "", false)
        ShireCoroutineScope.scope(project).launch {
            val suggestion = StringBuilder()

            flow?.cancellable()?.collect { char ->
                suggestion.append(char)

                invokeLater {
                    config.context.console.print(char, ConsoleViewContentType.NORMAL_OUTPUT)
                }
            }

            future.complete(suggestion.toString())
            config.postExecute.invoke(suggestion.toString(), null)
        }

        return future.get()
    }

    companion object {
        fun getInstance(): PasteManagerService =
            ApplicationManager.getApplication().getService(PasteManagerService::class.java)
    }
}

data class PasteProcessorConfig(
    val context: LocationInteractionContext,
    val postExecute: PostFunction,
    val processHandler: ProcessHandler,
)

class ShireCopyPastePreProcessor : CopyPastePreProcessor {
    override fun preprocessOnCopy(file: PsiFile, startOffsets: IntArray, endOffsets: IntArray, text: String): String? {
        return null
    }

    override fun preprocessOnPaste(
        project: Project,
        file: PsiFile,
        editor: Editor,
        text: String,
        rawText: RawText,
    ): String {
        val instance = PasteManagerService.getInstance()
        val hobbitHole = instance.firstProcessor() ?: return text

        return instance.executeProcessor(project, hobbitHole, text)
    }
}
