package com.phodal.shirecore.provider.codemodel

import com.intellij.lang.LanguageExtension
import com.intellij.psi.PsiFile
import com.phodal.shirecore.provider.codemodel.model.FileStructure

interface FileStructureProvider {
    fun build(psiFile: PsiFile): FileStructure?

    companion object {
        private val languageExtension = LanguageExtension<FileStructureProvider>("com.phodal.fileStructureProvider")
        private val providers: Map<String, FileStructureProvider> = StructureProvider.loadProviders(languageExtension)

        fun from(psiFile: PsiFile): FileStructure {
            return providers[psiFile.language.id]?.build(psiFile)
                ?: FileStructure(psiFile, psiFile.name, psiFile.virtualFile.path)
        }
    }
}
