package com.phodal.shirelang.java.codemodel

import com.intellij.openapi.application.runReadAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiVariable
import com.phodal.shirecore.codemodel.VariableStructureProvider
import com.phodal.shirecore.codemodel.model.VariableStructure
import com.phodal.shirelang.java.util.getContainingClass
import com.phodal.shirelang.java.util.getContainingMethod

class JavaVariableStructureProvider : VariableStructureProvider {
    override fun build(
        psiElement: PsiElement,
        withMethodContext: Boolean,
        withClassContext: Boolean,
        gatherUsages: Boolean,
    ): VariableStructure? {
        if (psiElement !is PsiVariable) return null

        val containingMethod = runReadAction {psiElement.getContainingMethod()  }
        val containingClass = runReadAction {  psiElement.getContainingClass()}

        val references =
            if (gatherUsages) JavaClassStructureProvider.findUsages(psiElement as PsiNameIdentifierOwner) else emptyList()

        return runReadAction {  VariableStructure(
            psiElement,
            psiElement.text ?: "",
            psiElement.name,
            containingMethod,
            containingClass,
            references,
            withMethodContext,
            withClassContext
        )}
    }
}
