package com.phodal.shirelang.javascript.codemodel

import com.intellij.lang.javascript.psi.JSFieldVariable
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.util.JSUtils
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.phodal.shirecore.provider.codemodel.VariableStructureProvider
import com.phodal.shirecore.provider.codemodel.model.VariableStructure

class JavaScriptVariableStructureProvider : VariableStructureProvider {
    override fun build(
        psiElement: PsiElement,
        withMethodContext: Boolean,
        withClassContext: Boolean,
        gatherUsages: Boolean
    ): VariableStructure? {
        if (psiElement !is JSFieldVariable) {
            return null
        }

        val parentOfType: PsiElement? = PsiTreeUtil.getParentOfType(psiElement, JSFunction::class.java, true)
        val memberContainingClass: PsiElement = JSUtils.getMemberContainingClass(psiElement)
        val psiReferences: List<PsiReference> = if (gatherUsages) {
            JavaScriptClassStructureProvider.findUsages(psiElement as PsiNameIdentifierOwner)
        } else {
            emptyList()
        }

        return VariableStructure(
            psiElement,
            psiElement.text,
            psiElement.name!!,
            parentOfType,
            memberContainingClass,
            psiReferences,
            withMethodContext,
            withClassContext
        )
    }
}