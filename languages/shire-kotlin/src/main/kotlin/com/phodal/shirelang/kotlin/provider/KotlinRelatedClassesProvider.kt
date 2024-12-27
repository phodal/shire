package com.phodal.shirelang.kotlin.provider

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.phodal.shirecore.provider.psi.RelatedClassesProvider
import com.phodal.shirelang.kotlin.KotlinTypeResolver

class KotlinRelatedClassesProvider : RelatedClassesProvider {
    override fun lookup(element: PsiElement): List<PsiElement> {
        return KotlinTypeResolver.resolveByElement(element).values.filterNotNull().toList()
    }

    override fun lookup(element: PsiFile): List<PsiElement> {
        return KotlinTypeResolver.resolveByElement(element).values.filterNotNull().toList()
    }
}
