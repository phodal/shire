package com.phodal.shirelang.saql

import com.intellij.lang.Language

class ShireAQLLanguage : Language("ShireAQL", "text/shireaql", "text/x-shireaql", "application/x-shireaql") {
    companion object {
        @JvmStatic
        val INSTANCE = ShireAQLLanguage()
    }

    override fun isCaseSensitive() = true
    override fun getDisplayName() = "ShireAQL"
}