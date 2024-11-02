package com.phodal.shirelang.proto.codemodel

import com.intellij.protobuf.lang.psi.PbDefinition
import com.intellij.protobuf.lang.psi.PbMessageDefinition
import com.intellij.protobuf.lang.psi.PbServiceDefinition
import com.intellij.protobuf.lang.psi.util.PbPsiUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.phodal.shirecore.provider.codemodel.ClassStructureProvider
import com.phodal.shirecore.provider.codemodel.model.ClassStructure

class ProtoClassStructureProvider : ClassStructureProvider {
    override fun build(psiElement: PsiElement, gatherUsages: Boolean): ClassStructure? {
        if (psiElement !is PbDefinition) return null

        return when (psiElement) {
            is PbMessageDefinition -> {
                val text = psiElement.text
                val name = psiElement.name

                val usages =
                    if (gatherUsages) findUsages(psiElement as PsiNameIdentifierOwner) else emptyList()

                val fields = mutableListOf<PsiElement>()
                psiElement.body?.simpleFieldList?.let { fields += it }
                psiElement.body?.mapFieldList?.let { fields += it }
                psiElement.body?.oneofDefinitionList?.let { fields += it }

                ClassStructure(
                    psiElement,
                    text,
                    name,
                    name,
                    emptyList(),
                    fields,
                    null,
                    emptyList(),
                    usages
                )
            }

            is PbServiceDefinition -> {
                val text = psiElement.text
                val name = psiElement.name

                val methods = psiElement.body?.serviceMethodList ?: emptyList()

                val usages =
                    if (gatherUsages) findUsages(psiElement as PsiNameIdentifierOwner) else emptyList()

                ClassStructure(
                    psiElement,
                    text,
                    name,
                    name,
                    methods,
                    emptyList(),
                    null,
                    emptyList(),
                    usages
                )
            }

            else -> {
                return null
            }
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
