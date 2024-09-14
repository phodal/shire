package com.phodal.shirelang.go.util

import com.goide.psi.*
import com.goide.psi.impl.GoPackage.GoPomTargetPsiElement
import com.goide.psi.impl.GoPsiImplUtil
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock

object GoPsiUtil {
    fun getDeclarationName(psiElement: PsiElement): String? {
        return singleNamedDescendant(psiElement)?.name
    }

    /**
     * Returns the single named descendant of the given [element].
     *
     * @param element the PsiElement to find the single named descendant from
     * @return the single named descendant of the given [element], or null if there is none
     */
    fun singleNamedDescendant(element: PsiElement): GoNamedElement? {
        return when (element) {
            is GoNamedElement -> element
            is GoTypeDeclaration -> element.typeSpecList.singleOrNull()
            is GoVarOrConstSpec<*> -> element.definitionList.singleOrNull()
            is GoVarOrConstDeclaration<*> -> {
                (element.specList.singleOrNull() as? GoVarOrConstSpec)?.definitionList?.singleOrNull()
            }

            is GoImportDeclaration -> element.importSpecList.singleOrNull()
            else -> null
        }
    }

    @RequiresReadLock
    fun findRelatedTypes(declaration: GoFunctionOrMethodDeclaration): List<GoTypeSpec> {
        val signature = declaration.signature ?: return emptyList()

        val parameterTypes = signature.parameters.parameterDeclarationList
            .mapNotNull { it.type }

        val resultTypes = when (val resultType = signature.resultType) {
            is GoTypeList -> resultType.typeList
            else -> listOf(resultType)
        }

        val mentionedTypes = parameterTypes + resultTypes

        val genericTypes = mentionedTypes
            .flatMap { it.typeArguments?.types ?: emptyList() }

        val relatedTypes = genericTypes + mentionedTypes

        return relatedTypes
            .mapNotNull { it.resolve(declaration) as? GoTypeSpec }
            .filter { isProjectContent(it) }
    }

    private fun isProjectContent(element: PsiElement): Boolean {
        val virtualFile = element.containingFile.virtualFile ?: return true
        return ProjectFileIndex.getInstance(element.project).isInContent(virtualFile)
    }

    private fun notInLibrary(element: PsiElement): Boolean {
        return !(element is GoPomTargetPsiElement ||
                ProjectFileIndex.getInstance(element.project).isInLibrary(element.containingFile.virtualFile))
    }

    private fun parentWithContext(element: PsiElement): PsiElement? {
        return when (element) {
            is GoTypeSpec, is GoMethodSpec, is GoFieldDefinition -> parentTypeSpecOrDeclaration(element)
            is GoMethodDeclaration -> {
                val resolveTypeSpec = element.resolveTypeSpec()
                resolveTypeSpec?.let { parentTypeSpecOrDeclaration(it) }
            }

            is GoVarOrConstDefinition -> parentVarOrConstSpecOrDeclaration(element)
            is GoImportSpec -> parentImportList(element)
            else -> element
        }
    }

    private fun parentImportList(importSpec: GoImportSpec): PsiElement {
        val importList = PsiTreeUtil.getParentOfType(importSpec, GoImportList::class.java, true)
        return if (importList?.importDeclarationList?.size == 1) importList else importSpec
    }

    private fun parentTypeSpecOrDeclaration(element: PsiElement): PsiElement {
        val typeSpec = PsiTreeUtil.getParentOfType(element, GoTypeSpec::class.java, false)
            ?: return element
        val typeDeclaration = PsiTreeUtil.getParentOfType(typeSpec, GoTypeDeclaration::class.java, true)
        return if (typeDeclaration?.typeSpecList?.size == 1) typeDeclaration else typeSpec
    }

    private fun parentVarOrConstSpecOrDeclaration(element: PsiElement): PsiElement {
        val varOrConstSpec = PsiTreeUtil.getParentOfType(element, GoVarOrConstSpec::class.java, false)
            ?: return element

        val varOrConstDeclaration =
            PsiTreeUtil.getParentOfType(varOrConstSpec, GoVarOrConstDeclaration::class.java, true)
                ?: return varOrConstSpec

        return when {
            varOrConstDeclaration.specList.size == 1 -> varOrConstDeclaration
            varOrConstDeclaration is GoConstDeclaration && varOrConstDeclaration.containsIota() -> varOrConstDeclaration
            else -> varOrConstSpec
        }
    }

    private fun GoExpression.containsIota(): Boolean {
        val traverser = GoPsiTreeUtil.goTraverser().withRoot(this)
        for (element in traverser.traverse()) {
            if (GoPsiImplUtil.isIota(element)) {
                return true
            }
        }
        return false
    }

    private fun GoConstDeclaration.containsIota(): Boolean {
        return constSpecList
            .asSequence()
            .flatMap { it.expressionList.asSequence() }
            .any { it.containsIota() }
    }

}