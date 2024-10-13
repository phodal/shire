package com.phodal.shirelang.highlight.braces

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.phodal.shirelang.psi.ShireTypes

class ShireBraceMatcher : PairedBraceMatcher {
    override fun getPairs(): Array<BracePair> {
        return arrayOf(
            BracePair(ShireTypes.OPEN_BRACE, ShireTypes.CLOSE_BRACE, true),
            BracePair(ShireTypes.CODE_BLOCK_START, ShireTypes.CODE_BLOCK_END, true)
        )
    }

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, type: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset
}