package com.phodal.shirelang.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.phodal.shirelang.lexer.ShireLexerAdapter
import com.phodal.shirelang.psi.ShireTypes

class ShireSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = ShireLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return pack(ATTRIBUTES[tokenType])
    }

    companion object {
        private val ATTRIBUTES: MutableMap<IElementType, TextAttributesKey> = HashMap()

        private val KEYWORDS: TokenSet = TokenSet.create(
            ShireTypes.CASE,
            ShireTypes.DEFAULT,
            ShireTypes.SELECT,
            ShireTypes.WHERE,
            ShireTypes.FROM,
            ShireTypes.IF,
            ShireTypes.ELSE,
            ShireTypes.ELSEIF,
            ShireTypes.END,
            ShireTypes.ENDIF,
            ShireTypes.AND,

            // true and false
            ShireTypes.BOOLEAN,

            // lifecycle
            ShireTypes.WHEN,
            ShireTypes.BEFORE_STREAMING,
            ShireTypes.ON_STREAMING,
            ShireTypes.ON_STREAMING_END,
            ShireTypes.AFTER_STREAMING,
        )

        init {
            fillMap(
                ATTRIBUTES,
                KEYWORDS,
                DefaultLanguageHighlighterColors.KEYWORD
            )

            ATTRIBUTES[ShireTypes.COMMENTS] = DefaultLanguageHighlighterColors.LINE_COMMENT
            ATTRIBUTES[ShireTypes.CONTENT_COMMENTS] = DefaultLanguageHighlighterColors.LINE_COMMENT
            ATTRIBUTES[ShireTypes.BLOCK_COMMENT] = DefaultLanguageHighlighterColors.BLOCK_COMMENT

            ATTRIBUTES[ShireTypes.VARIABLE_START] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.VARIABLE_ID] = DefaultLanguageHighlighterColors.CONSTANT

            ATTRIBUTES[ShireTypes.FOREIGN_TYPE] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.OUTPUT_VAR] = DefaultLanguageHighlighterColors.LOCAL_VARIABLE
            ATTRIBUTES[ShireTypes.ACCESS] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.PROCESS] = DefaultLanguageHighlighterColors.KEYWORD

            ATTRIBUTES[ShireTypes.AGENT_START] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.AGENT_ID] = DefaultLanguageHighlighterColors.CONSTANT

            ATTRIBUTES[ShireTypes.COMMAND_START] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.COMMAND_ID] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.COMMAND_PROP] = DefaultLanguageHighlighterColors.STRING

            ATTRIBUTES[ShireTypes.SHARP] = DefaultLanguageHighlighterColors.CONSTANT
            ATTRIBUTES[ShireTypes.MARKDOWN_HEADER] = DefaultLanguageHighlighterColors.CONSTANT

            ATTRIBUTES[ShireTypes.LINE_INFO] = DefaultLanguageHighlighterColors.NUMBER

            ATTRIBUTES[ShireTypes.CODE_BLOCK_START] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.CODE_BLOCK_END] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.LANGUAGE_ID] = DefaultLanguageHighlighterColors.CONSTANT

            ATTRIBUTES[ShireTypes.NUMBER] = DefaultLanguageHighlighterColors.NUMBER

            ATTRIBUTES[ShireTypes.FRONTMATTER_START] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.FRONTMATTER_END] = DefaultLanguageHighlighterColors.KEYWORD

            ATTRIBUTES[ShireTypes.FRONT_MATTER_ID] = DefaultLanguageHighlighterColors.CONSTANT

            // func name
            ATTRIBUTES[ShireTypes.IDENTIFIER] = DefaultLanguageHighlighterColors.IDENTIFIER
            ATTRIBUTES[ShireTypes.NUMBER] = DefaultLanguageHighlighterColors.KEYWORD
            ATTRIBUTES[ShireTypes.QUOTE_STRING] = DefaultLanguageHighlighterColors.STRING
            ATTRIBUTES[ShireTypes.DATE] = DefaultLanguageHighlighterColors.LABEL

            ATTRIBUTES[ShireTypes.LBRACKET] = DefaultLanguageHighlighterColors.BRACKETS
            ATTRIBUTES[ShireTypes.RBRACKET] = DefaultLanguageHighlighterColors.BRACKETS

            ATTRIBUTES[ShireTypes.OPEN_BRACE] = DefaultLanguageHighlighterColors.BRACES
            ATTRIBUTES[ShireTypes.CLOSE_BRACE] = DefaultLanguageHighlighterColors.BRACES

            ATTRIBUTES[ShireTypes.LPAREN] = DefaultLanguageHighlighterColors.PARENTHESES
            ATTRIBUTES[ShireTypes.RPAREN] = DefaultLanguageHighlighterColors.PARENTHESES
        }
    }

}
