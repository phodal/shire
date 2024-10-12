package com.phodal.shirelang.highlight.braces

import com.intellij.codeInsight.highlighting.PairedBraceMatcherAdapter
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.phodal.shirelang.psi.ShireTypes
import org.intellij.plugins.markdown.lang.MarkdownLanguage
import org.intellij.plugins.markdown.lang.MarkdownTokenTypes

class ShireBraceMatcher : PairedBraceMatcherAdapter(
    MyPairedBraceMatcher(), MarkdownLanguage.INSTANCE
) {
    class MyPairedBraceMatcher : PairedBraceMatcher {
        override fun getPairs(): Array<BracePair> {
            return arrayOf(
                BracePair(ShireTypes.LPAREN, ShireTypes.RPAREN, false),
                BracePair(ShireTypes.LBRACKET, ShireTypes.RBRACKET, false),
                BracePair(ShireTypes.LT, ShireTypes.GT, false),
                BracePair(ShireTypes.CODE_BLOCK_START, ShireTypes.CODE_BLOCK_END, true)
            )
        }

        override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, type: IElementType?): Boolean {
            return type == null || type === MarkdownTokenTypes.WHITE_SPACE || type === MarkdownTokenTypes.EOL || type === MarkdownTokenTypes.RPAREN || type === MarkdownTokenTypes.RBRACKET || type === MarkdownTokenTypes.GT
        }

        override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int {
            return openingBraceOffset
        }
    }
}
