package com.phodal.shirelang.javascript.provider

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.phodal.shirecore.provider.psi.RelatedClassesProvider
import com.phodal.shirelang.javascript.JSTypeResolver

class JavaScriptRelatedClassesProvider : RelatedClassesProvider {
    override fun lookup(element: PsiElement): List<PsiElement> {
        return JSTypeResolver.resolveByElement(element)
    }

    override fun lookup(element: PsiFile): List<PsiElement> {
        return JSTypeResolver.resolveByElement(element)
    }
}
