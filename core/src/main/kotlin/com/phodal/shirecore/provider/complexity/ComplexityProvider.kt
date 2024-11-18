package com.phodal.shirecore.provider.complexity

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.phodal.shirecore.provider.codemodel.StructureProvider

interface ComplexityProvider {
    companion object {
        private val languageExtension = LanguageExtension<ComplexityProvider>("com.phodal.complexityProvider")
        private val providers: Map<String, ComplexityProvider> = StructureProvider.loadProviders(languageExtension)

        fun provide(language: Language): ComplexityProvider? {
            return providers[language.id]
        }
    }
}
