// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.phodal.shirelang

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.elementType
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.psi.ShireTypes
import org.jetbrains.kotlin.utils.addToStdlib.UnsafeCastFunction
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class ShireTypedHandler : TypedHandlerDelegate() {
    override fun checkAutoPopup(charTyped: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (file !is ShireFile) return Result.CONTINUE

        return when (charTyped) {
            '`' -> {
                val offset = editor.caretModel.primaryCaret.offset
                if (offset == 0) return Result.CONTINUE

                val element = file.findElementAt(offset - 1)
                if (element?.elementType == ShireTypes.CODE_CONTENT || element?.elementType == ShireTypes.CODE_BLOCK_END) {
                    return Result.CONTINUE
                }

                PsiDocumentManager.getInstance(project).commitDocument(editor.document)
                AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null)
                return Result.STOP
            }

            '@', '/', '$', ':' -> {
                PsiDocumentManager.getInstance(project).commitDocument(editor.document)
                AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null)
                Result.STOP
            }

            else -> {
                Result.CONTINUE
            }
        }
    }


    override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
        if (file.fileType != ShireFileType.INSTANCE) return Result.CONTINUE

        return Result.CONTINUE
    }


    @OptIn(UnsafeCastFunction::class)
    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (file.fileType != ShireFileType.INSTANCE) return Result.CONTINUE

        when {
            c == '{' && CodeInsightSettings.getInstance().AUTOINSERT_PAIR_BRACKET -> {
                PsiDocumentManager.getInstance(project).commitDocument(editor.document)
                val offset = editor.caretModel.offset
                val previousElement = file.findElementAt(offset - 1)

                if (previousElement is LeafPsiElement) {
                    val identifier = file.findElementAt(offset)
                        ?.safeAs<LeafPsiElement>()
                        ?.takeIf { it.elementType == ShireTypes.IDENTIFIER || it.elementType == ShireTypes.QUOTE_STRING }
                        ?: kotlin.run {
                            editor.document.insertString(offset, "}")
                            return Result.STOP
                        }

                    // todo: add more logic here
                }
            }
        }

        return Result.CONTINUE
    }
}
