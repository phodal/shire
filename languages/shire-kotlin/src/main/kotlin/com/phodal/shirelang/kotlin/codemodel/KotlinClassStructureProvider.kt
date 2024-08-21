package com.phodal.shirelang.kotlin.codemodel

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.phodal.shirecore.codemodel.ClassStructureProvider
import com.phodal.shirecore.codemodel.model.ClassStructure
import com.phodal.shirelang.kotlin.KotlinPsiUtil
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtParameter

class KotlinClassStructureProvider : ClassStructureProvider {
    private fun getPrimaryConstructorFields(kotlinClass: KtClassOrObject): List<KtParameter> {
        return kotlinClass.getPrimaryConstructorParameters().filter { it.hasValOrVar() }
    }

    override fun build(psiElement: PsiElement, gatherUsages: Boolean): ClassStructure? {
        if (psiElement !is KtClassOrObject) return null

        val text = psiElement.text
        val name = psiElement.name
        val functions = KotlinPsiUtil.getFunctions(psiElement)
        val allFields = getPrimaryConstructorFields(psiElement)
        val usages =
            if (gatherUsages) KotlinPsiUtil.findUsages(psiElement as PsiNameIdentifierOwner) else emptyList()

        val annotations: List<String> = psiElement.annotationEntries.mapNotNull {
            it.text
        }

        val displayName = psiElement.fqName?.asString() ?: psiElement.name ?: ""
        return ClassStructure(
            psiElement,
            text,
            name,
            displayName,
            functions,
            allFields,
            null,
            annotations = annotations,
            usages
        )
    }
}

