package com.phodal.shirelang.lexer

import com.intellij.psi.tree.IElementType
import com.phodal.shirelang.ShireLanguage
import org.jetbrains.annotations.NonNls

class ShireTokenType(@NonNls debugName: String) : IElementType(debugName, ShireLanguage.INSTANCE) {
    override fun toString(): String = "ShireTokenType." + super.toString()
}
