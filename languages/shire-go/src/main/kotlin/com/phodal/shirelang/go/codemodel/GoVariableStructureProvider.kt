package com.phodal.shirelang.go.codemodel

import com.goide.psi.GoVarOrConstDefinition
import com.intellij.psi.PsiElement
import com.phodal.shirecore.codemodel.VariableStructureProvider
import com.phodal.shirecore.codemodel.model.VariableStructure

class GoVariableStructureProvider : VariableStructureProvider {
    override fun build(
        psiElement: PsiElement,
        withMethodContext: Boolean,
        withClassContext: Boolean,
        gatherUsages: Boolean,
    ): VariableStructure? {
        if (psiElement !is GoVarOrConstDefinition) {
            return null
        }

        val name = psiElement.name

        return VariableStructure(
            psiElement, psiElement.text, name, null, null, emptyList(), false, false
        )
    }
}
