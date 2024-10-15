package com.phodal.shirelang.kotlin.codemodel

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.phodal.shirecore.provider.codemodel.MethodStructureProvider
import com.phodal.shirecore.provider.codemodel.model.MethodStructure
import com.phodal.shirelang.kotlin.KotlinPsiUtil
import org.jetbrains.kotlin.idea.quickfix.createFromUsage.callableBuilder.getReturnTypeReference
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass

class KotlinMethodStructureProvider : MethodStructureProvider {
    override fun build(psiElement: PsiElement, includeClassContext: Boolean, gatherUsages: Boolean): MethodStructure? {
        if (psiElement !is KtNamedFunction) return null

        val returnType = psiElement.getReturnTypeReference()?.text
        val containingClass = psiElement.containingClass()
        val signatureString = KotlinPsiUtil.signatureString(psiElement)
        val displayName = psiElement.language.displayName
        val valueParameters = psiElement.valueParameters.mapNotNull { it.name }
        val usages =
            if (gatherUsages) KotlinPsiUtil.findUsages(psiElement as PsiNameIdentifierOwner) else emptyList()

        return MethodStructure(
            psiElement,
            psiElement.text,
            psiElement.name,
            signatureString,
            containingClass,
            displayName,
            returnType,
            valueParameters,
            includeClassContext,
            usages
        )
    }
}
