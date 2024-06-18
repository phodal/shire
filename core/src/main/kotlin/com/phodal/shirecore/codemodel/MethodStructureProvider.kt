package com.phodal.shirecore.codemodel

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.psi.PsiElement
import com.phodal.shirecore.codemodel.model.MethodStructure

/**
 * The MethodContextBuilder interface provides a method for retrieving the method context of a given PsiElement.
 * A method context represents the context in which a method is defined or used within a codebase.
 * @see MethodStructure
 */
interface MethodStructureProvider {
    fun build(psiElement: PsiElement, includeClassContext: Boolean, gatherUsages: Boolean): MethodStructure?

    companion object {
        private val languageExtension = LanguageExtension<MethodStructureProvider>("com.phodal.methodStructureProvider")
        private val providers: List<MethodStructureProvider>

        init {
            val registeredLanguages = Language.getRegisteredLanguages()
            providers = registeredLanguages.mapNotNull(languageExtension::forLanguage)
        }

        fun from(psiElement: PsiElement, includeClassContext: Boolean = false, gatherUsages: Boolean = false): MethodStructure? {
            val iterator = providers.iterator()
            while (iterator.hasNext()) {
                val provider = iterator.next()
                val methodContext = provider.build(psiElement, includeClassContext, gatherUsages)
                if (methodContext != null) {
                    return methodContext
                }
            }

            return MethodStructure(psiElement, psiElement.text, null)
        }
    }
}
