package com.phodal.shirelang

import com.intellij.lang.Language

object ShireLanguage : Language("Shire", "text/shire", "text/x-shire", "application/x-shire") {
    override fun isCaseSensitive() = true
    override fun getDisplayName() = "Shire"
}
