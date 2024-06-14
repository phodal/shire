package com.phodal.shirecore.provider

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension

/**
 * Resolve variables for code struct generation.
 * This is used to provide the variables that are used in the code struct generation.
 *
 * - currentClass
 * - relatedClasses
 * - similarTestCase
 * - imports
 * - isNewFile
 * - targetTestFileName
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
