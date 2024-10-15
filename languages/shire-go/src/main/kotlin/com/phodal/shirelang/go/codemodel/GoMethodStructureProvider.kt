package com.phodal.shirelang.go.codemodel

import com.goide.psi.GoFunctionDeclaration
import com.goide.psi.GoFunctionOrMethodDeclaration
import com.goide.psi.GoMethodDeclaration
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.codemodel.MethodStructureProvider
import com.phodal.shirecore.provider.codemodel.model.MethodStructure

class GoMethodStructureProvider : MethodStructureProvider {
    override fun build(psiElement: PsiElement, includeClassContext: Boolean, gatherUsages: Boolean): MethodStructure? {
        if (psiElement !is GoFunctionOrMethodDeclaration) {
            return null
        }


        val funcName = psiElement.name ?: ""

        val functionSignature: String = when (psiElement) {
            is GoMethodDeclaration -> {
                psiElement.signature?.text ?: ""
            }

            is GoFunctionDeclaration -> {
                psiElement.signature?.text ?: ""
            }

            else -> ""
        }
        val returnType = psiElement.signature?.resultType?.text
        val languages = psiElement.language.displayName
        val returnTypeText = returnType
        val parameterNames = psiElement.signature?.parameters?.parameterDeclarationList?.mapNotNull {
            it.paramDefinitionList.firstOrNull()?.text
        }.orEmpty()

        return MethodStructure(
            psiElement, psiElement.text, funcName, functionSignature, null, languages,
            returnTypeText, parameterNames, includeClassContext, emptyList()
        )

    }
}
