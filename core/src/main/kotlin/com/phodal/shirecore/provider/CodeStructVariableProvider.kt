package com.phodal.shirecore.provider

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension

/**
 * Resolve variables for code struct generation.
 * This is used to provide the variables that are used in the code struct generation.
 */
interface CodeStructVariableProvider {
    companion object {
        private val languageExtension: LanguageExtension<CodeStructVariableProvider> =
            LanguageExtension("com.phodal.shireCodeStructVariableProvider")

        fun provide(language: Language): CodeStructVariableProvider? {
            return languageExtension.forLanguage(language)
        }
    }
}

/**
 * Code Structure variables
 * - currentClass: The current class for which the code struct is being generated.
 * - relatedClasses: Any related classes that are relevant to the code struct generation.
 * - similarTestCase: Any similar test cases that can be used as reference for the code struct generation.
 * - imports: The necessary imports required for the code struct generation.
 * - isNewFile: A flag indicating whether the code struct is being generated in a new file.
 * - targetTestFileName: The name of the target test file where the code struct will be generated.
 */
enum class CodeStructVariable {
    /**
     * return PsiNameIdentifierOwner of the current class, so we can get the class name
     */
    CURRENT_CLASS,
    /**
     * Input and output of PsiElement and PsiFile
     */
    RELATED_CLASSES,
    /**
     * Use TfIDF to search code for similar test case
     */
    SIMILAR_TEST_CASE,

    /**
     * Import statements for the code struct
     */
    IMPORTS,

    /**
     * Flag indicating whether the code struct is being generated in a new file
     */
    IS_NEW_FILE,

    /**
     * The name of the target test file where the code struct will be generated
     */
    TARGET_TEST_FILE_NAME
}
