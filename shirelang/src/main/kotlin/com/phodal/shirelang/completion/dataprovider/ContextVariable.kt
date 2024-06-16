package com.phodal.shirelang.completion.dataprovider

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

enum class ContextVariable(val variable: String, val description: String) {
    SELECTION("selection", "The selected text"),
    BEFORE_CURSOR("beforeCursor", "The text before the cursor"),
    AFTER_CURSOR("afterCursor", "The text after the cursor"),
    FILE_NAME("fileName", "The name of the file"),
    FILE_PATH("filePath", "The path of the file"),
    METHOD_NAME("methodName", "The name of the method"),
    LANGUAGE("language", "The language of the file"),
    COMMENT_SYMBOL("commentSymbol", "The comment symbol of the language"),
    FRAMEWORK_CONTEXT("frameworkContext", "The context of the framework"),
    ALL("all", "All the text")
    ;

    companion object {
        fun all(): List<ContextVariable> = values().toList()
        fun resolve(editor: Editor, element: PsiElement?): Map<out String, String> {
            val file = element?.containingFile

            return all().associate {
                it.variable to when (it) {
                    SELECTION -> editor.selectionModel.selectedText ?: ""
                    BEFORE_CURSOR -> element?.text?.substring(0, editor.caretModel.offset) ?: ""
                    AFTER_CURSOR -> element?.text?.substring(editor.caretModel.offset) ?: ""
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

                    FRAMEWORK_CONTEXT -> ""
                    ALL -> file?.text ?: ""
                }
            }
        }
    }
}
