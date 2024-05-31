package com.phodal.shirelang

import com.intellij.lang.Language

object ShireLanguage : Language("Shire", "text/shire", "text/x-shire", "application/x-shire") {
    val INSTANCE: Language = ShireLanguage
    override fun isCaseSensitive() = true
    override fun getDisplayName() = "Shire"
}
