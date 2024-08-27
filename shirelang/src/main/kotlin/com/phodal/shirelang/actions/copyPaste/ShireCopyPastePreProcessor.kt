package com.phodal.shirelang.actions.copyPaste

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RawText
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.phodal.shirecore.config.interaction.PostFunction
import com.phodal.shirecore.provider.ide.LocationInteractionContext
import com.phodal.shirelang.compiler.hobbit.HobbitHole

@Service(Service.Level.APP)
class PasteManagerService {
    private val pasteProcessorMap = mutableMapOf<HobbitHole, PasteProcessorConfig>()

    fun registerPasteProcessor(key: HobbitHole, config: PasteProcessorConfig) {
        pasteProcessorMap[key] = config
    }

    fun filterPasteProcessor(key: HobbitHole): PasteProcessorConfig? {
        return pasteProcessorMap[key]
    }

    fun hasProcessor(): Boolean {
        return pasteProcessorMap.isNotEmpty()
    }

    fun executeProcessor(key: HobbitHole, text: String) {
        val config = pasteProcessorMap[key] ?: return
        config.postExecute.invoke(text, null)

        try {
            config.processHandler.detachProcess()
        } catch (e: Exception) {
            config.context.console.print(e.message ?: "Error", ConsoleViewContentType.ERROR_OUTPUT)
        }
    }

    companion object {
        fun getInstance(): PasteManagerService =
            ApplicationManager.getApplication().getService(PasteManagerService::class.java)
    }
}

data class PasteProcessorConfig (
    val context: LocationInteractionContext,
    val postExecute: PostFunction,
    val processHandler: ProcessHandler
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
        if (!instance.hasProcessor()) {
            return text
        }

        return text
    }
}
