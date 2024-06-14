package com.phodal.shirecore.provider

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.psi.PsiElement

/**
 * Enum representing variables used in the generation of code structures.
 */
enum class PsiVariable(val variableName: String) {
    /**
     * Represents the PsiNameIdentifierOwner of the current class, used to retrieve the class name.
     */
    CURRENT_CLASS_NAME("currentClassName"),

    /**
     * Represents the input and output of PsiElement and PsiFile.
     */
    CURRENT_CLASS_CODE("currentClassCode"),

    /**
     * Represents the input and output of PsiElement and PsiFile.
     */
    RELATED_CLASSES("relatedClasses"),

    /**
     * Uses TfIDF to search for similar test cases in the code.
     */
    SIMILAR_TEST_CASE("similarTestCase"),

    /**
     * Represents the import statements required for the code structure.
     */
    IMPORTS("imports"),

    /**
     * Flag indicating whether the code structure is being generated in a new file.
     */
    IS_NEW_FILE("isNewFile"),

    /**
     * The name of the target test file where the code structure will be generated.
     */
    TARGET_TEST_FILE_NAME("targetTestFileName")
}

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