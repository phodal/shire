package com.phodal.shirecore.provider.variable

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement

/**
 * Resolve variables for code struct generation.
 * This is used to provide the variables that are used in the code struct generation.
 */
interface PsiContextVariableProvider {
    /**
     * Calculate the value for the given variable based on the provided PsiElement.
     *
     * @param psiElement the PsiElement to use for resolving the variable value
     * @param variable the PsiVariable for which to calculate the value
     * @return the calculated value for the variable as a String
     */
    fun resolve(variable: PsiContextVariable, psiElement: PsiElement?, editor: Editor): Any

    companion object {
        private val languageExtension: LanguageExtension<PsiContextVariableProvider> =
            LanguageExtension("com.phodal.shirePsiVariableProvider")

        fun provide(language: Language): PsiContextVariableProvider {
            return languageExtension.forLanguage(language)  ?: DefaultPsiContextVariableProvider()
        }
    }
}
