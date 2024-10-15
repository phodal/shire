package com.phodal.shirelang.kotlin.codemodel

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil
import com.phodal.shirecore.provider.codemodel.VariableStructureProvider
import com.phodal.shirecore.provider.codemodel.model.VariableStructure
import com.phodal.shirelang.kotlin.KotlinPsiUtil
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.psi.psiUtil.containingClass

class KotlinVariableStructureProvider : VariableStructureProvider {
    override fun build(
        psiElement: PsiElement,
        withMethodContext: Boolean,
        withClassContext: Boolean,
        gatherUsages: Boolean
    ): VariableStructure? {
        when (psiElement) {
            is KtVariableDeclaration -> {
                val text = psiElement.text
                val name = psiElement.name
                val parentOfType = PsiTreeUtil.getParentOfType(psiElement, KtNamedFunction::class.java, true)
                val containingClass = psiElement.containingClass()
                val psiNameIdentifierOwner = psiElement as? PsiNameIdentifierOwner

                val usages = if (gatherUsages && psiNameIdentifierOwner != null) {
                    KotlinPsiUtil.findUsages(psiNameIdentifierOwner)
                } else {
                    emptyList()
                }

                return VariableStructure(psiElement, text, name, parentOfType, containingClass, usages, withMethodContext, withClassContext)
            }

            is KtParameter -> {
                val text = psiElement.text
                val name = psiElement.name
                val parentOfType = PsiTreeUtil.getParentOfType(psiElement, KtNamedFunction::class.java, true)
                val containingClass = psiElement.containingClass()
                val psiNameIdentifierOwner = psiElement as? PsiNameIdentifierOwner

                val usages = if (gatherUsages && psiNameIdentifierOwner != null) {
                    KotlinPsiUtil.findUsages(psiNameIdentifierOwner)
                } else {
                    emptyList()
                }

                return VariableStructure(psiElement, text, name, parentOfType, containingClass, usages, withMethodContext, withClassContext)
            }

            else -> {
                return null
            }
        }
    }
}
