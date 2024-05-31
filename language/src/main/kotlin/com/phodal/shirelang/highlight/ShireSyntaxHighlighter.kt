package com.phodal.shirelang.highlight

import com.phodal.shirelang.psi.ShireTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import com.phodal.shirelang.lexer.ShireLexerAdapter

class ShireSyntaxHighlighter : SyntaxHighlighter {
    override fun getHighlightingLexer(): Lexer = ShireLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return SyntaxHighlighterBase.pack(ATTRIBUTES[tokenType])
    }

    companion object {
        private val ATTRIBUTES: MutableMap<IElementType, TextAttributesKey> = HashMap()

        init {
            ATTRIBUTES[ShireTypes.COMMENTS] = DefaultLanguageHighlighterColors.LINE_COMMENT

            ATTRIBUTES[ShireTypes.VARIABLE_START] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.VARIABLE_ID] = DefaultLanguageHighlighterColors.CONSTANT

            ATTRIBUTES[ShireTypes.AGENT_START] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.AGENT_ID] = DefaultLanguageHighlighterColors.CONSTANT

            ATTRIBUTES[ShireTypes.COMMAND_START] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.COMMAND_ID] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.COMMAND_PROP] = DefaultLanguageHighlighterColors.STRING

            ATTRIBUTES[ShireTypes.SHARP] = DefaultLanguageHighlighterColors.CONSTANT
            ATTRIBUTES[ShireTypes.LINE_INFO] = DefaultLanguageHighlighterColors.NUMBER

            ATTRIBUTES[ShireTypes.CODE_BLOCK_START] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.CODE_BLOCK_END] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.LANGUAGE_ID] = DefaultLanguageHighlighterColors.CONSTANT

            ATTRIBUTES[ShireTypes.SYSTEM_START] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.SYSTEM_ID] = DefaultLanguageHighlighterColors.CONSTANT
            ATTRIBUTES[ShireTypes.NUMBER] = DefaultLanguageHighlighterColors.NUMBER
        }
    }

}
