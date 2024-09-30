package com.phodal.shirelang.parser

import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import com.phodal.shirelang.psi.ShireTypes

object ShireTokenTypeSets {
    // ShireTypes.NEWLINE
    val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

    val SHIRE_COMMENTS = TokenSet.create(ShireTypes.CONTENT_COMMENTS, ShireTypes.COMMENTS, ShireTypes.BLOCK_COMMENT)
}
