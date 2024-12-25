package com.phodal.shirelang.java.provider

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.*
import com.intellij.psi.util.*
import com.phodal.shirecore.provider.psi.RelatedClassesProvider

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

    override fun lookup(element: PsiFile): List<PsiElement> {
        return when (element) {
            is PsiJavaFile -> findRelatedClasses(element.classes.first())
            else -> emptyList()
        }
    }

    private fun findRelatedClasses(clazz: PsiClass): List<PsiClass> {
        return ApplicationManager.getApplication().executeOnPooledThread<List<PsiClass>> {
            runReadAction { clazz.allMethods }
                .flatMap { findRelatedClasses(it) }.distinct()
        }.get()
    }

    /**
     * Finds related classes to the given PsiMethod by analyzing its parameters, return type, and generic types.
     *
     * @param method the PsiMethod for which related classes need to be found
     * @return a list of PsiClass instances that are related to the given PsiMethod, filtered to include only classes that are part of the project content
     */
    private fun findRelatedClasses(method: PsiMethod): List<PsiClass> = runReadAction {
        val parameters = method.parameterList.parameters
        val parameterTypes = parameters.map { it.type }

        val genericTypes = parameters.flatMap { (it.type as? PsiClassType)?.parameters?.toList() ?: emptyList() }
        val mentionedTypes = parameterTypes + genericTypes

        val filterIsInstance = mentionedTypes.filterIsInstance<PsiClassType>()
            .distinct()

        return@runReadAction ApplicationManager.getApplication().executeOnPooledThread<List<PsiClass>> {
            return@executeOnPooledThread filterIsInstance
                .mapNotNull { runReadAction { it.resolve() } }
                .filter { isProjectContent(it) }
                .toList()
        }.get()
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
        return ApplicationManager.getApplication().executeOnPooledThread<Boolean> {
            runReadAction {
                ProjectFileIndex.getInstance(element.project).isInSourceContent(virtualFile)
            }
        }.get()
    }
}