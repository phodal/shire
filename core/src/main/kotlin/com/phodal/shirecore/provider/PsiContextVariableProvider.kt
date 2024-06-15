package com.phodal.shirecore.provider

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

/**
 * Resolve variables for code struct generation.
 * This is used to provide the variables that are used in the code struct generation.
 */
interface PsiContextVariableProvider {
    /**
     * Calculate the values for the given variable based on the provided PsiElement.
     *
     * @param psiElement the PsiElement for which to calculate variable values
     * @return a map containing the resolved values for each PsiVariable based on the provided PsiElement
     */
    fun resolveAll(psiElement: PsiElement): Map<PsiVariable, String> {
        val result = mutableMapOf<PsiVariable, String>()
        for (variable in PsiVariable.values()) {
            result[variable] = resolveVariableValue(psiElement, variable)
        }
        return result
    }

    /**
     * Calculate the value for the given variable based on the provided PsiElement.
     *
     * @param psiElement the PsiElement to use for resolving the variable value
     * @param variable the PsiVariable for which to calculate the value
     * @return the calculated value for the variable as a String
     */
    fun resolveVariableValue(psiElement: PsiElement, variable: PsiVariable): String

    companion object {
        private val languageExtension: LanguageExtension<PsiContextVariableProvider> =
            LanguageExtension("com.phodal.shirePsiVariableProvider")

        fun provide(language: Language): PsiContextVariableProvider {
            return languageExtension.forLanguage(language)  ?: DefaultPsiContextVariableProvider()
        }
    }
}

class DefaultPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolveVariableValue(psiElement: PsiElement, variable: PsiVariable): String {
        return ""
    }
}