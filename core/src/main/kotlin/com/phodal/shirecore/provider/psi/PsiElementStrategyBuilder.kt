package com.phodal.shirecore.provider.psi

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.phodal.shirecore.codemodel.model.ClassStructure

interface PsiElementStrategyBuilder {

    /**
     * Looks up a symbol in the given project by its canonical name.
     * The canonical name is the fully qualified name of the symbol, including the package name.
     * For example, the canonical name of the class `java.lang.String` is `java.lang.String`.
     * The canonical name of the method `java.lang.String#length` is `java.lang.String.length()`.
     *
     * @param project the project in which to search for the symbol
     * @param canonicalName the canonical name of the symbol to look up
     * @return the ClassStructure representing the symbol with the given canonical name, or null if not found
     */
    fun lookupElement(project: Project, canonicalName: String): ClassStructure?

    /**
     * Return the relative [PsiElement] with [PsiComment] for givenElement in the given project.
     */
    fun relativeElement(project: Project, givenElement: PsiElement, type: PsiComment): PsiElement?

    /**
     * Find the nearest target [PsiNameIdentifierOwner] for the given [PsiElement].
     */
    fun findNearestTarget(psiElement: PsiElement): PsiNameIdentifierOwner?

    companion object {
        private val languageExtension: LanguageExtension<PsiElementStrategyBuilder> =
            LanguageExtension("com.phodal.shireElementStrategyBuilder")

        fun forLanguage(language: Language): PsiElementStrategyBuilder? {
            return languageExtension.forLanguage(language)
        }
    }
}

