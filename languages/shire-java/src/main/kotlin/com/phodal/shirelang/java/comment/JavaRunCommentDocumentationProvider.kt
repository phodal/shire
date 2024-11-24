package com.phodal.shirelang.java.comment

import com.intellij.lang.documentation.DocumentationProviderEx
import com.intellij.psi.PsiElement

class JavaRunCommentDocumentationProvider : DocumentationProviderEx() {
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        return super.generateDoc(element, originalElement)
    }
}
