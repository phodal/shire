package com.phodal.shirelang.java.impl

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.phodal.shirecore.codemodel.model.ClassStructure
import com.phodal.shirecore.provider.variable.PsiElementStrategyBuilder
import com.phodal.shirelang.java.codemodel.JavaClassStructureProvider

class JavaElementStrategyBuilder : PsiElementStrategyBuilder {
    override fun lookupElement(project: Project, canonicalName: String): ClassStructure? {
        val psiClass: PsiClass = JavaPsiFacade.getInstance(project)
            .findClass(canonicalName, GlobalSearchScope.projectScope(project))
            ?: return null

        return JavaClassStructureProvider().build(psiClass, false)
    }

    override fun relativeElement(project: Project, givenElement: PsiElement, type: PsiComment): PsiElement? {
        TODO("Not yet implemented")
    }

    fun findNearestTarget(psiElement: PsiElement): PsiNameIdentifierOwner? {
        if (psiElement is PsiMethod || psiElement is PsiClass) return psiElement as PsiNameIdentifierOwner

        val closestIdentifierOwner = PsiTreeUtil.getParentOfType(psiElement, PsiNameIdentifierOwner::class.java)
        if (closestIdentifierOwner !is PsiMethod) {
            return PsiTreeUtil.getParentOfType(psiElement, PsiMethod::class.java) ?: closestIdentifierOwner
        }

        return closestIdentifierOwner
    }

//    fun findSampleDocument()
}
