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
    ALL("all", "All the text")
    ;
}
