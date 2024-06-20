package com.phodal.shirelang.java.variable.provider

import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import com.phodal.shirecore.provider.psi.RelatedClassesProvider

/**
 * This class JavaRelatedClassesProvider implements the RelatedClassesProvider interface and provides a method to
 * lookup related classes for a given PsiElement, specifically a PsiMethod.
 * It analyzes the parameters, return type, and generic types of the PsiMethod to find related classes that are part
 * of the project content.
 * It also includes methods to clean up unnecessary elements in a PsiClass, find superclasses of a PsiClass, and determine
 * if an element is part of the project content.
 */
class JavaRelatedClassesProvider : RelatedClassesProvider {
    override fun lookup(element: PsiElement): List<PsiClass> {
        return when (element) {
            is PsiMethod -> findRelatedClasses(element)
                .flatMap { findSuperClasses(it) }
                .map { cleanUp(it) }
                .toList()

            is PsiClass -> findRelatedClasses(element)
            else -> emptyList()
        }
    }

    private fun findRelatedClasses(clazz: PsiClass): List<PsiClass> {
        return clazz.allMethods.flatMap { findRelatedClasses(it) }.distinct()
    }

    /**
     * Finds related classes to the given PsiMethod by analyzing its parameters, return type, and generic types.
     *
     * @param method the PsiMethod for which related classes need to be found
     * @return a list of PsiClass instances that are related to the given PsiMethod, filtered to include only classes that are part of the project content
     */
    private fun findRelatedClasses(method: PsiMethod): List<PsiClass> {
        val parameters = method.parameterList.parameters
        val parameterTypes = parameters.map { it.type }

        val genericTypes = parameters.flatMap { (it.type as? PsiClassType)?.parameters?.toList() ?: emptyList() }
        val mentionedTypes = parameterTypes + method.returnType + genericTypes

        return mentionedTypes.filterIsInstance<PsiClassType>()
            .mapNotNull { it.resolve() }
            .filter { isProjectContent(it) }
            .toList()
    }

    /**
     * Cleans up a given PsiClass by removing unnecessary elements such as method bodies, method comments, and any other removable members.
     *
     * @param psiClass the PsiClass to be cleaned up
     * @return a new PsiClass with the unnecessary elements removed
     */
    private fun cleanUp(psiClass: PsiClass): PsiClass {
        val psiElement = psiClass.copy() as PsiClass
        psiElement.containingFile.setName(psiClass.containingFile.name)

        val members = PsiTreeUtil.findChildrenOfType(psiElement, PsiMember::class.java)

        members.filterIsInstance<PsiMethod>().forEach {
            it.body?.delete()
            it.docComment?.delete()
        }
        members.filter { canBeRemoved(it) }.forEach { it.delete() }

        psiElement.docComment?.delete()
        return psiElement
    }

    override fun cleanUp(psiClass: PsiElement): PsiElement {
        return cleanUp(psiClass as PsiClass)
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
        if (member.modifierList?.hasModifierProperty("public") == true) return false
        return member.annotations.isEmpty()
    }

    private fun isProjectContent(element: PsiElement): Boolean {
        val virtualFile = PsiUtil.getVirtualFile(element) ?: return false
        return ProjectFileIndex.getInstance(element.project).isInContent(virtualFile)
    }
}