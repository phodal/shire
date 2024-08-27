package com.phodal.shirelang.actions.copyPaste

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RawText
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class ShireCopyPastePreProcessor : CopyPastePreProcessor {
    override fun preprocessOnCopy(file: PsiFile, startOffsets: IntArray, endOffsets: IntArray, text: String): String? {
        return null
    }

    override fun preprocessOnPaste(
        project: Project,
        file: PsiFile,
        editor: Editor,
        text: String,
        rawText: RawText,
    ): String {
//        val caretPsiElement = PsiUtilCore.getElementAtOffset(file, editor.caretModel.currentCaret.offset)
//        val prevElement = PsiTreeUtil.skipWhitespacesAndCommentsBackward(caretPsiElement)
        return text
    }
}
