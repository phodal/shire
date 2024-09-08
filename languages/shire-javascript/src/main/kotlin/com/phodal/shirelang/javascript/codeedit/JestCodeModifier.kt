package com.phodal.shirelang.javascript.codeedit

import com.intellij.lang.Language
import com.phodal.shirelang.javascript.util.LanguageApplicableUtil

class JestCodeModifier : JavaScriptTestCodeModifier() {
    override fun isApplicable(language: Language): Boolean {
        return LanguageApplicableUtil.isJavaScriptApplicable(language)
    }
}