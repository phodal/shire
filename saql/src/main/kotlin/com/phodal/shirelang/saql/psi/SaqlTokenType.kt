package com.phodal.shirelang.saql.psi

import com.intellij.psi.tree.IElementType
import com.phodal.shirelang.saql.SaqlLanguage

class SaqlTokenType(debugName: String) : IElementType(debugName, SaqlLanguage.INSTANCE) {
    override fun toString(): String = when (val token = super.toString()) {
        "," -> "comma"
        ";" -> "semicolon"
        "'" -> "single quote"
        "\"" -> "double quote"
        else -> token
    }
}
