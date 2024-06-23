package com.phodal.shirelang

import com.intellij.application.options.CodeStyle
import com.intellij.codeInsight.generation.CommenterDataHolder
import com.intellij.codeInsight.generation.SelfManagingCommenter
import com.intellij.codeInsight.generation.SelfManagingCommenterUtil
import com.intellij.lang.Commenter
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.util.text.CharArrayUtil

data class CommentHolder(val file: PsiFile) : CommenterDataHolder() {
    fun useSpaceAfterLineComment(): Boolean = CodeStyle.getLanguageSettings(file, ShireLanguage.INSTANCE).LINE_COMMENT_ADD_SPACE
}

class ShireCommenter : Commenter, SelfManagingCommenter<CommentHolder> {
    override fun getLineCommentPrefix(): String = "//"

    override fun getBlockCommentPrefix(): String = "/*"
    override fun getBlockCommentSuffix(): String = "*/"

    override fun getCommentedBlockCommentPrefix(): String = "*//*"
    override fun getCommentedBlockCommentSuffix(): String = "*//*"

    private val LINE_PREFIXES: List<String> = listOf("//")

    override fun getBlockCommentPrefix(
        selectionStart: Int,
        document: Document,
        data: CommentHolder
    ): String = blockCommentPrefix

    override fun getBlockCommentSuffix(
        selectionEnd: Int,
        document: Document,
        data: CommentHolder
    ): String = blockCommentSuffix

    override fun getBlockCommentRange(
        selectionStart: Int,
        selectionEnd: Int,
        document: Document,
        data: CommentHolder
    ): TextRange? = SelfManagingCommenterUtil.getBlockCommentRange(
        selectionStart,
        selectionEnd,
        document,
        blockCommentPrefix,
        blockCommentSuffix
    )

    override fun insertBlockComment(
        startOffset: Int,
        endOffset: Int,
        document: Document,
        data: CommentHolder?
    ): TextRange = SelfManagingCommenterUtil.insertBlockComment(
        startOffset,
        endOffset,
        document,
        blockCommentPrefix,
        blockCommentSuffix
    )

    override fun uncommentBlockComment(
        startOffset: Int,
        endOffset: Int,
        document: Document,
        data: CommentHolder?
    ) = SelfManagingCommenterUtil.uncommentBlockComment(
        startOffset,
        endOffset,
        document,
        blockCommentPrefix,
        blockCommentSuffix
    )

    override fun commentLine(line: Int, offset: Int, document: Document, data: CommentHolder) {
        val addSpace = data.useSpaceAfterLineComment()
        document.insertString(offset, "//" + if (addSpace) " " else "")
    }

    override fun getCommentPrefix(line: Int, document: Document, data: CommentHolder): String = lineCommentPrefix

    override fun createBlockCommentingState(
        selectionStart: Int,
        selectionEnd: Int,
        document: Document,
        file: PsiFile
    ): CommentHolder = CommentHolder(file)

    override fun isLineCommented(line: Int, offset: Int, document: Document, data: CommentHolder): Boolean {
        return LINE_PREFIXES.any { CharArrayUtil.regionMatches(document.charsSequence, offset, it) }
    }

    override fun uncommentLine(line: Int, offset: Int, document: Document, data: CommentHolder) {
        val addSpace = data.useSpaceAfterLineComment()
        document.insertString(offset, "//" + if (addSpace) " " else "")
    }

    override fun createLineCommentingState(
        startLine: Int,
        endLine: Int,
        document: Document,
        file: PsiFile
    ): CommentHolder = CommentHolder(file)
}

