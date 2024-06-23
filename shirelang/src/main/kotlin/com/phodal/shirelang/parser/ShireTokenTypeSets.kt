package com.phodal.shirelang.parser

import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import com.phodal.shirelang.psi.ShireTypes

object ShireTokenTypeSets {
    val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

    val SHIRE_COMMENTS = TokenSet.create(ShireTypes.COMMENT, ShireTypes.COMMENTS, ShireTypes.BLOCK_COMMENT)
}
