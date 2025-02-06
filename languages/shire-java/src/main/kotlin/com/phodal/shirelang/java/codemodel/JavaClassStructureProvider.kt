package com.phodal.shirelang.java.codemodel

import com.intellij.openapi.application.runReadAction
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.MethodReferencesSearch
import com.intellij.psi.search.searches.ReferencesSearch
import com.phodal.shirecore.provider.codemodel.ClassStructureProvider
import com.phodal.shirecore.provider.codemodel.model.ClassStructure

class JavaClassStructureProvider : ClassStructureProvider {
    override fun build(psiElement: PsiElement, gatherUsages: Boolean): ClassStructure? {
        if (psiElement !is PsiClass) return null

        val supers = runReadAction {
            psiElement.extendsList?.referenceElements?.mapNotNull {
                it.text
            }
        }

        return runReadAction {
            val fields = psiElement.fields.toList()
            val methods = psiElement.methods.toList()

            val usages =
                if (gatherUsages) findUsages(psiElement as PsiNameIdentifierOwner) else emptyList()

            val annotations: List<String> = psiElement.annotations.mapNotNull {
                it.text
            }

            ClassStructure(
                psiElement, psiElement.text, psiElement.name,
                displayName = psiElement.qualifiedName,
                methods, fields, supers,
                annotations,
                usages
            )
        }
    }

    companion object {
        /**
         * This method is used to find usages of a given PsiNameIdentifierOwner in the project.
         *
         * @param nameIdentifierOwner the PsiNameIdentifierOwner for which usages need to be found
         * @return a list of PsiReference objects representing the usages of the given PsiNameIdentifierOwner
         */
        fun findUsages(nameIdentifierOwner: PsiNameIdentifierOwner): List<PsiReference> {
            val project = nameIdentifierOwner.project
            val searchScope = GlobalSearchScope.allScope(project) as SearchScope

            return when (nameIdentifierOwner) {
                is PsiMethod -> {
                    MethodReferencesSearch.search(nameIdentifierOwner, searchScope, true)
                }

                else -> {
                    ReferencesSearch.search((nameIdentifierOwner as PsiElement), searchScope, true)
                }
            }.findAll().map { it as PsiReference }
        }
    }
}
