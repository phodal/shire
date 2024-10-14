package com.phodal.shirelang.psi

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.phodal.shirelang.ShireLanguage

class ShireElementType(debugName: String) : IElementType(debugName, ShireLanguage.INSTANCE) {
    companion object {
        val SPACE_ELEMENTS: TokenSet = TokenSet.create(
            TokenType.WHITE_SPACE,
            ShireTypes.INDENT
        )

        val CONTAINERS: TokenSet = TokenSet.create(
            ShireTypes.CODE_BLOCK,
            ShireTypes.FRONT_MATTER_ENTRY,
        )
    }
}