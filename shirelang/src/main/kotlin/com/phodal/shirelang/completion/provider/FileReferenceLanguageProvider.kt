package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.ide.presentation.VirtualFilePresentation
import com.intellij.openapi.fileEditor.impl.EditorHistoryManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ProcessingContext
import com.phodal.shirecore.canBeAdded
import org.jetbrains.annotations.NonNls
import java.io.File

class FileReferenceLanguageProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val project = parameters.position.project
        val basePath = project.guessProjectDir()?.path ?: return

        /**
         * Recent open files
         */
        EditorHistoryManager.getInstance(project).fileList.forEach {
            if (!it.canBeAdded(project)) return@forEach
            result.addElement(buildElement(it, basePath))
        }

        /**
         * Project Files
         */
        ProjectFileIndex.getInstance(project).iterateContent {
            if (!it.canBeAdded(project)) return@iterateContent true
            result.addElement(buildElement(it, basePath))
            true
        }
    }

    private fun buildElement(
        virtualFile: VirtualFile,
        basePath: @NonNls String
    ): LookupElement {
        val removePrefix = virtualFile.path.removePrefix(basePath)
        val relativePath: String = removePrefix.removePrefix(File.separator)

        val elementBuilder = LookupElementBuilder.create(relativePath)
            .withIcon(VirtualFilePresentation.getIcon(virtualFile))
            .withInsertHandler { context, _ ->
                context.editor.caretModel.moveCaretRelatively(
                    1, 0, false, false, false
                )
            }

        return PrioritizedLookupElement.withPriority(elementBuilder, 1.0)
    }
}

