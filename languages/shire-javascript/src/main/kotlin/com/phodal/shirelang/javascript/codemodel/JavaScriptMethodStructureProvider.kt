package com.phodal.shirelang.javascript.codemodel

import com.intellij.lang.javascript.presentable.JSFormatUtil
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.JSType
import com.intellij.lang.javascript.psi.util.JSUtils
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.phodal.shirecore.codemodel.MethodStructureProvider
import com.phodal.shirecore.codemodel.model.MethodStructure

class JavaScriptMethodStructureProvider : MethodStructureProvider {
    override fun build(psiElement: PsiElement, includeClassContext: Boolean, gatherUsages: Boolean): MethodStructure? {
        if (psiElement !is JSFunction) return null

        val functionSignature = JSFormatUtil.buildFunctionSignaturePresentation(psiElement)
        val containingClass: PsiElement? = JSUtils.getMemberContainingClass(psiElement)
        val languageDisplayName = psiElement.language.displayName
        val returnType = psiElement.returnType
        val returnTypeText = returnType?.substitute()?.getTypeText(JSType.TypeTextFormat.CODE)

        val parameterNames = psiElement.parameters.mapNotNull { it.name }

        val usages =
            if (gatherUsages) JavaScriptClassStructureProvider.findUsages(psiElement as PsiNameIdentifierOwner) else emptyList()

        return MethodStructure(
            psiElement, psiElement.text, psiElement.name!!, psiElement.name + functionSignature, containingClass, languageDisplayName,
            returnTypeText, parameterNames, includeClassContext, usages
        )
    }
}