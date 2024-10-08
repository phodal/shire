package com.phodal.shirelang.parser

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.phodal.shirelang.ShireLanguage
import com.phodal.shirelang.lexer.ShireLexerAdapter
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.psi.ShireTypes
import org.jetbrains.annotations.NotNull


internal class ShireParserDefinition : ParserDefinition {
    @NotNull
    override fun createLexer(project: Project?): Lexer = ShireLexerAdapter()

    @NotNull
    override fun getCommentTokens(): TokenSet = ShireTokenTypeSets.SHIRE_COMMENTS

    @NotNull
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun getWhitespaceTokens(): TokenSet = ShireTokenTypeSets.WHITESPACES

    @NotNull
    override fun createParser(project: Project?): PsiParser = ShireParser()

    @NotNull
    override fun getFileNodeType(): IFileElementType = FILE

    @NotNull
    override fun createFile(@NotNull viewProvider: FileViewProvider): PsiFile = ShireFile(viewProvider)

    @NotNull
    override fun createElement(node: ASTNode?): PsiElement {
        return when (node!!.elementType) {
            ShireTypes.CODE -> {
                CodeBlockElement(node)
            }

            ShireTypes.PATTERN -> {
                PatternElement(node)
            }
            ShireTypes.FUNC_CALL -> {
                when (node.firstChildNode.text) {
                    "grep" -> {
                        ShireGrepFuncCall(node)
                    }
                    "sed" -> {
                        ShireSedFuncCall(node)
                    }
                    else -> {
                        ShireTypes.Factory.createElement(node)
                    }
                }
            }
            ShireTypes.CODE_CONTENTS -> {
                ASTWrapperPsiElement(node)
            }

            else -> ShireTypes.Factory.createElement(node)
        }
    }

    companion object {
        val FILE: IFileElementType = IFileElementType(ShireLanguage.INSTANCE)
    }
}