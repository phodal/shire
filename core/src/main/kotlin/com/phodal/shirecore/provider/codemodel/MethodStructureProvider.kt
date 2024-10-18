package com.phodal.shirecore.provider.codemodel

import com.intellij.lang.LanguageExtension
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.codemodel.model.MethodStructure

/**
 * The MethodContextBuilder interface provides a method for retrieving the method context of a given PsiElement.
 * A method context represents the context in which a method is defined or used within a codebase.
 * @see MethodStructure
 */
interface MethodStructureProvider {
    fun build(psiElement: PsiElement, includeClassContext: Boolean, gatherUsages: Boolean): MethodStructure?

    companion object {
        private val languageExtension = LanguageExtension<MethodStructureProvider>("com.phodal.methodStructureProvider")
        private val providers: Map<String, MethodStructureProvider> = StructureProvider.loadProviders(languageExtension)

        fun from(psiElement: PsiElement, includeClassContext: Boolean = false, gatherUsages: Boolean = false): MethodStructure? {
            return providers[psiElement.language.id]?.build(psiElement, includeClassContext, gatherUsages)
                ?: MethodStructure(psiElement, psiElement.text, null)
        }
    }
}
