package com.phodal.shirelang.java.impl

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.phodal.shirecore.codemodel.model.ClassStructure
import com.phodal.shirecore.provider.PsiElementStrategyBuilder
import com.phodal.shirelang.java.codemodel.JavaClassStructureProvider

class JavaElementStrategyBuilder : PsiElementStrategyBuilder {
    override fun lookupElement(project: Project, canonicalName: String): ClassStructure? {
        val psiClass: PsiClass = JavaPsiFacade.getInstance(project)
            .findClass(canonicalName, GlobalSearchScope.projectScope(project))
            ?: return null

        return JavaClassStructureProvider().getClassStructure(psiClass, false)
    }

    override fun relativeElement(project: Project, givenElement: PsiElement, type: PsiComment): PsiElement? {
        TODO("Not yet implemented")
    }
}
