package com.phodal.shirelang.java.variable.provider

import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil

class JavaRelatedClassesProvider {
    fun lookup(method: PsiMethod): List<PsiClass> {
        val relatedClasses = findRelatedClasses(method)
        val cleanedUpClasses = relatedClasses.flatMap { findSuperClasses(it) }.map { cleanUp(it) }
        return cleanedUpClasses.toList()
    }

    fun cleanUp(psiClass: PsiClass): PsiClass {
        val psiElement = psiClass.copy() as PsiClass
        psiElement.containingFile.setName(psiClass.containingFile.name)
        val members = PsiTreeUtil.findChildrenOfType(psiElement, PsiMember::class.java)

        for (element in members) {
            if (element is PsiMethod) {
                element.body?.delete()
                element.docComment?.delete()
            }
        }

        val removableMembers = members.filter { canBeRemoved(it) }
        removableMembers.forEach { it.delete() }

        psiElement.docComment?.delete()
        return psiElement
    }

    private fun findSuperClasses(psiClass: PsiClass): List<PsiClass> {
        val superClass = psiClass.superClass ?: return emptyList()
        if (isProjectContent(superClass)) {
            return listOf(psiClass.superClass!!, psiClass)
        }

        if (isProjectContent(psiClass)) {
            return listOf(psiClass)
        }

        return emptyList()
    }

    private fun canBeRemoved(member: PsiMember): Boolean {
        val modifierList = member.modifierList
        if (modifierList?.hasModifierProperty("public") != true) {
            val annotations = member.annotations
            if (annotations.isEmpty()) {
                return true
            }
        }

        return false
    }

    private fun findRelatedClasses(method: PsiMethod): List<PsiClass> {
        val parameters = method.parameterList.parameters
        val parameterTypes = parameters.map { it.type }
        val genericTypes = parameters.flatMap { (it.type as? PsiClassType)?.parameters?.toList() ?: emptyList() }
        val mentionedTypes = parameterTypes + method.returnType + genericTypes
        val relatedTypes = mentionedTypes.filterIsInstance<PsiClassType>()
        val resolvedClasses = relatedTypes.mapNotNull { it.resolve() }
        val projectContentClasses = resolvedClasses.filter { isProjectContent(it) }
        return projectContentClasses.toList()
    }

    fun isProjectContent(element: PsiElement): Boolean {
        val virtualFile = PsiUtil.getVirtualFile(element)
        return virtualFile == null || ProjectFileIndex.getInstance(element.project).isInContent(virtualFile)
    }
}