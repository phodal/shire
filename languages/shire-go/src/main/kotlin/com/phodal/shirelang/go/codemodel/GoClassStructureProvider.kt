package com.phodal.shirelang.go.codemodel

import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoTypeDeclaration
import com.goide.psi.GoTypeSpec
import com.intellij.psi.PsiElement
import com.phodal.shirecore.codemodel.ClassStructureProvider
import com.phodal.shirecore.codemodel.model.ClassStructure
import com.phodal.shirelang.go.util.GoPsiUtil

class GoClassStructureProvider : ClassStructureProvider {
    override fun build(psiElement: PsiElement, gatherUsages: Boolean): ClassStructure? {
        if (psiElement !is GoTypeDeclaration && psiElement !is GoTypeSpec) {
            return null
        }

        val typeSpecs: List<GoTypeSpec> = when (psiElement) {
            is GoTypeSpec -> listOf(psiElement)
            is GoTypeDeclaration -> psiElement.typeSpecList
            else -> emptyList()
        }

        val methodPairs = typeSpecs.flatMap { type ->
            val methods = type.methods
            methods.map { method -> method to type.name }
        }

        val methods = methodPairs.map { it.first }
            .filterIsInstance<GoMethodDeclaration>()

        val name = GoPsiUtil.getDeclarationName(psiElement)

        return ClassStructure(
            psiElement, psiElement.text, name, name, methods, emptyList(), emptyList(), emptyList()
        )
    }
}
