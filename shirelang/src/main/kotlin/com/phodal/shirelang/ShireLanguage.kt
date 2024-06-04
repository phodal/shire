package com.phodal.shirelang

import com.intellij.lang.Language

class ShireLanguage : Language("Shire", "text/shire", "text/x-shire", "application/x-shire") {
    companion object {
        @JvmStatic
        val INSTANCE = ShireLanguage()
    }

    override fun isCaseSensitive() = true
    override fun getDisplayName() = "Shire"
}
