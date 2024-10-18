package com.phodal.shirecore.provider.codemodel

import com.intellij.lang.LanguageExtension
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.codemodel.model.VariableStructure

interface VariableStructureProvider {
    fun build(
        psiElement: PsiElement,
        withMethodContext: Boolean,
        withClassContext: Boolean,
        gatherUsages: Boolean,
    ): VariableStructure?

    companion object {
        private val languageExtension =
            LanguageExtension<VariableStructureProvider>("com.phodal.variableStructureProvider")
        private val providers: Map<String, VariableStructureProvider> = StructureProvider.loadProviders(languageExtension)

        fun from(
            psiElement: PsiElement,
            includeMethodContext: Boolean = false,
            includeClassContext: Boolean = false,
            gatherUsages: Boolean = false,
        ): VariableStructure {
            return providers[psiElement.language.id]?.build(psiElement, includeMethodContext, includeClassContext, gatherUsages)
                ?: VariableStructure(psiElement, psiElement.text, null)
        }
    }

}