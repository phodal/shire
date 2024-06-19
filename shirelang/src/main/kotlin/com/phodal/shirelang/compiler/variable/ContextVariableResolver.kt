package com.phodal.shirelang.compiler.variable

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.phodal.shirelang.completion.dataprovider.ContextVariable
import com.phodal.shirelang.completion.dataprovider.ContextVariable.*

class ContextVariableResolver(
    val editor: Editor, val element: PsiElement?
): VariableResolver {
    fun all(): List<ContextVariable> = values().toList()

    override fun resolve(): Map<String, String> {
        val file = element?.containingFile

        return all().associate {
            it.variable to when (it) {
                SELECTION -> editor.selectionModel.selectedText ?: ""
                BEFORE_CURSOR -> file?.text?.substring(0, editor.caretModel.offset) ?: ""
                AFTER_CURSOR -> file?.text?.substring(editor.caretModel.offset) ?: ""
                FILE_NAME -> file?.name ?: ""
                FILE_PATH -> file?.virtualFile?.path ?: ""
                METHOD_NAME -> when (element) {
                    is PsiNameIdentifierOwner -> element.nameIdentifier?.text ?: ""
                    else -> ""
                }

                LANGUAGE -> element?.language?.displayName ?: ""
                COMMENT_SYMBOL -> when (element?.language?.displayName?.lowercase()) {
                    "java", "kotlin" -> "//"
                    "python" -> "#"
                    "javascript" -> "//"
                    "typescript" -> "//"
                    "go" -> "//"
                    "c", "c++", "c#" -> "//"
                    "rust" -> "//"
                    "ruby" -> "#"
                    "shell" -> "#"
                    else -> "-"
                }

                ALL -> file?.text ?: ""
            }
        }
    }
}