package com.phodal.shirecore.codemodel.model

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.phodal.shirecore.codemodel.base.FormatableElement

class ClassStructure(
    override val root: PsiElement,
    override val text: String?,
    override val name: String?,
    val displayName: String?,
    val methods: List<PsiElement> = emptyList(),
    val fields: List<PsiElement> = emptyList(),
    val superClasses: List<String>? = null,
    val annotations: List<String> = mutableListOf(),
    val usages: List<PsiReference> = emptyList(),
) : FormatableElement(root, text, name) {

}