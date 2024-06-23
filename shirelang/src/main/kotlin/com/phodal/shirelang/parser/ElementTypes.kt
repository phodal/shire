package com.phodal.shirelang.parser

import com.intellij.psi.tree.TokenSet
import com.phodal.shirelang.psi.ShireTypes.*

val SHIRE_COMMENTS = TokenSet.create(COMMENT, COMMENTS, BLOCK_COMMENT)