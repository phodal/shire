package com.phodal.shirecore.provider.codemodel

import com.intellij.lang.LanguageExtension
import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.codemodel.model.ClassStructure

/**
 * The ClassContextBuilder interface provides a method to retrieve the class context for a given PsiElement.
 * The class context represents the surrounding context of a class, including its imports, package declaration,
 * and any other relevant information.
 */
interface ClassStructureProvider {
    /**
     * Retrieves the class context for the given [psiElement].
     *
     * @param psiElement the PSI element for which to retrieve the class context
     * @param gatherUsages specifies whether to gather usages of the class
     * @return the class context for the given [psiElement], or null if the class context cannot be determined
     */
    fun build(psiElement: PsiElement, gatherUsages: Boolean): ClassStructure?

    companion object {
        private val languageExtension = LanguageExtension<ClassStructureProvider>("com.phodal.classStructureProvider")
        private val providers: Map<String, ClassStructureProvider> = StructureProvider.loadProviders(languageExtension)
        private val logger = logger<ClassStructureProvider>()

        fun from(psiElement: PsiElement, gatherUsages: Boolean = false): ClassStructure? {
            try {
                return providers[psiElement.language.id]?.build(psiElement, gatherUsages)
            } catch (e: Exception) {
                logger.error("Error while getting class context from", e)
            }
            return null
        }
    }
}
