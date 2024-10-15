package com.phodal.shirecore.provider.codemodel

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.psi.PsiFile
import com.phodal.shirecore.provider.codemodel.model.FileStructure

interface FileStructureProvider {
    fun build(psiFile: PsiFile): FileStructure?

    companion object {
        private val languageExtension = LanguageExtension<FileStructureProvider>("com.phodal.fileStructureProvider")
        private val providers: List<FileStructureProvider>

        init {
            val registeredLanguages = Language.getRegisteredLanguages()
            providers = registeredLanguages.mapNotNull(languageExtension::forLanguage)
        }

        fun from(psiFile: PsiFile): FileStructure {
            for (provider in providers) {
                val fileContext = provider.build(psiFile)
                if (fileContext != null) {
                    return fileContext
                }
            }

            return FileStructure(psiFile, psiFile.name, psiFile.virtualFile.path)
        }
    }
}
