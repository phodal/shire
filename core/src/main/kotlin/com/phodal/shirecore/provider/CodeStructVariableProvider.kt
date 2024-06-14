package com.phodal.shirecore.provider

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.psi.PsiElement

/**
 * Enum representing variables used in the generation of code structures.
 */
enum class CodeStructVariable(val variableName: String) {
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
interface CodeStructVariableProvider {
    /**
     * Calculate the values for the given variable based on the provided PsiElement.
     */
    fun calculate(psiElement: PsiElement): Map<CodeStructVariable, String> {
        val result = mutableMapOf<CodeStructVariable, String>()
        for (variable in CodeStructVariable.values()) {
            result[variable] = calculateVariable(psiElement, variable)
        }
        return result
    }

    /**
     * Calculate the value for the given variable based on the provided PsiElement.
     */
    fun calculateVariable(psiElement: PsiElement, variable: CodeStructVariable): String

    companion object {
        private val languageExtension: LanguageExtension<CodeStructVariableProvider> =
            LanguageExtension("com.phodal.shireCodeStructVariableProvider")

        fun provide(language: Language): CodeStructVariableProvider {
            return languageExtension.forLanguage(language)  ?: DefaultCodeStructVariableProvider()
        }
    }
}

class DefaultCodeStructVariableProvider : CodeStructVariableProvider {
    override fun calculateVariable(psiElement: PsiElement, variable: CodeStructVariable): String {
        return ""
    }
}