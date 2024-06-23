package com.phodal.shirelang.parser

import com.intellij.psi.tree.TokenSet
import com.phodal.shirelang.psi.ShireTypes.COMMENT
import com.phodal.shirelang.psi.ShireTypes.COMMENTS

val SHIRE_COMMENTS = TokenSet.create(COMMENT, COMMENTS)