package com.phodal.shirelang.saql

import com.intellij.lang.Language

class SaqlLanguage : Language("Saql", "text/saql", "text/x-saql", "application/x-saql") {
    companion object {
        @JvmStatic
        val INSTANCE = SaqlLanguage()
    }

    override fun isCaseSensitive() = true
    override fun getDisplayName() = "Saql"
}