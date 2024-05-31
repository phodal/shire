package com.phodal.shirelang.lexer

import com.intellij.psi.tree.IElementType
import com.phodal.shirelang.ShireLanguage
import org.jetbrains.annotations.NonNls

class ShireTokenType(debugName: @NonNls String) : IElementType(debugName, ShireLanguage) {
    override fun toString(): String = "ShireTokenType." + super.toString()
}