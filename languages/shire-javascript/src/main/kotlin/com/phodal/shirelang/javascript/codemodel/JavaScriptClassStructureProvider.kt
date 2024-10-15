package com.phodal.shirelang.javascript.codemodel

import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.openapi.application.runReadAction
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.phodal.shirecore.provider.codemodel.ClassStructureProvider
import com.phodal.shirecore.provider.codemodel.model.ClassStructure

class JavaScriptClassStructureProvider : ClassStructureProvider {
    override fun build(psiElement: PsiElement, gatherUsages: Boolean): ClassStructure? {
        when (psiElement) {
            is JSClass -> {
                val methods: List<PsiElement> = psiElement.functions.toList()
                val fields: List<PsiElement> = psiElement.fields.toList()

                val usages =
                    if (gatherUsages) findUsages(psiElement as PsiNameIdentifierOwner) else emptyList()

                val supers = psiElement.supers
                val superClasses = supers.filterIsInstance<JSClass>().mapNotNull { it.name }

                val annotations: List<String> = mutableListOf()

                return ClassStructure(
                    psiElement,
                    psiElement.text,
                    psiElement.name,
                    displayName = runReadAction { psiElement.qualifiedName },
                    methods,
                    fields,
                    superClasses,
                    annotations,
                    usages
                )
            }
            else -> return null
        }
    }

    companion object {
        fun findUsages(psiElement: PsiElement): List<PsiReference> {
            val globalSearchScope = GlobalSearchScope.allScope(psiElement.project)

            return ReferencesSearch.search(psiElement, globalSearchScope, true)
                .findAll()
                .toList()
        }
    }
}