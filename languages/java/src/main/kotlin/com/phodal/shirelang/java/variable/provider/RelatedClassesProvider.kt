package com.phodal.shirelang.java.variable.provider

import com.intellij.psi.PsiElement

interface RelatedClassesProvider<T> {
    fun lookupMethod(method: PsiElement): List<T>
}