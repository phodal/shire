package com.phodal.shirecore.provider.codemodel

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension

/**
 * @author lk
 */
object StructureProvider {

    private val registeredLanguages = Language.getRegisteredLanguages()

    /**
     * Load providers for different StructureProviders
     * and return specific providers based on the language.
     */
    fun <T> loadProviders(languageExtension: LanguageExtension<T>): Map<String, T> {
        return registeredLanguages.mapNotNull {
            languageExtension.forLanguage(it)?.run {
                it.id to this
            }
        }.toMap()
    }
}