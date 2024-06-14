package com.phodal.shirelang.java.toolchain

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiMethod

fun PsiElement.getContainingMethod(): PsiMethod? {
    var context: PsiElement? = this.context
    while (context != null) {
        if (context is PsiMethod) return context

        context = context.context
    }

    return null
}

fun PsiElement.getContainingClass(): PsiClass? {
    var context: PsiElement? = this.context
    while (context != null) {
        if (context is PsiClass) return context
        if (context is PsiMember) return context.containingClass

        context = context.context
    }

    return null
}