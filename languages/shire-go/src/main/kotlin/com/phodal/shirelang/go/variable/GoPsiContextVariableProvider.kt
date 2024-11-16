package com.phodal.shirelang.go.variable

import com.goide.GoLanguage
import com.goide.psi.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.TestSourcesFilter
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testIntegration.TestFinderHelper
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.impl.CodeSmellBuilder
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirecore.search.similar.SimilarChunksSearch
import com.phodal.shirelang.go.codemodel.GoClassStructureProvider
import com.phodal.shirelang.go.codemodel.GoMethodStructureProvider
import com.phodal.shirelang.go.util.GoPsiUtil

class GoPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolve(
        variable: PsiContextVariable,
        project: Project,
        editor: Editor,
        psiElement: PsiElement?,
    ): Any {
        if (psiElement?.language !is GoLanguage) return ""

        val underTestElement = getElementForTests(psiElement)
        val underTestFile = underTestElement?.containingFile as? GoFile ?: return ""

        return when (variable) {
            PsiContextVariable.CURRENT_CLASS_NAME -> ""
            PsiContextVariable.CURRENT_CLASS_CODE -> {
                when (underTestElement) {
                    is GoTypeDeclaration,
                    is GoTypeSpec,
                        -> {
                        GoClassStructureProvider().build(underTestElement, false)?.format()
                    }

                    is GoFunctionOrMethodDeclaration -> GoMethodStructureProvider()
                        .build(underTestElement, false, false)
                        ?.format()

                    else -> null
                }
            }

            PsiContextVariable.CURRENT_METHOD_NAME -> {
                when (psiElement) {
                    is GoFunctionOrMethodDeclaration -> psiElement.name
                    else -> psiElement.text
                }
            }

            PsiContextVariable.CURRENT_METHOD_CODE -> psiElement.text
            PsiContextVariable.RELATED_CLASSES -> {
                when (underTestElement) {
                    is GoFunctionOrMethodDeclaration -> {
                        GoPsiUtil.findRelatedTypes(underTestElement).map { it.text}
                    }

                    is GoFile -> {
                        val functions = underTestElement.functions
                        val methods = underTestElement.methods

                        (functions + methods).flatMap {
                            GoPsiUtil.findRelatedTypes(it)
                        }.map { it.text}
                    }

                    else -> emptyList()
                }
            }

            PsiContextVariable.SIMILAR_TEST_CASE -> TODO()

            PsiContextVariable.IMPORTS -> {
                val importList = PsiTreeUtil.getChildrenOfTypeAsList(underTestFile, GoImportDeclaration::class.java)
                importList.map { it.text }
            }

            PsiContextVariable.IS_NEED_CREATE_FILE -> TODO()
            PsiContextVariable.TARGET_TEST_FILE_NAME -> {
                val name = GoPsiUtil.getDeclarationName(underTestElement) ?: return ""
                toTestFileName(name)
            }

            PsiContextVariable.UNDER_TEST_METHOD_CODE -> {
                when (underTestElement) {
                    is GoFunctionOrMethodDeclaration -> psiElement.text
                    else -> psiElement.text
                }
            }

            PsiContextVariable.FRAMEWORK_CONTEXT -> return collectFrameworkContext(psiElement, project)
            PsiContextVariable.CODE_SMELL -> CodeSmellBuilder.collectElementProblemAsSting(
                underTestElement,
                project,
                editor
            )

            PsiContextVariable.METHOD_CALLER -> {
                if (psiElement !is GoFunctionOrMethodDeclaration) return ""
                ""
            }

            PsiContextVariable.CALLED_METHOD -> return SimilarChunksSearch.createQuery(psiElement) ?: ""
            PsiContextVariable.SIMILAR_CODE -> TODO()
            PsiContextVariable.STRUCTURE -> {
                when (underTestElement) {
                    is GoTypeDeclaration,
                    is GoTypeSpec,
                        -> {
                        GoClassStructureProvider().build(underTestElement, true)?.toString() ?: ""
                    }

                    is GoFunctionOrMethodDeclaration -> GoMethodStructureProvider()
                        .build(underTestElement, true, true)
                        ?.toString() ?: ""
                    else -> ""
                }
            }
            PsiContextVariable.CHANGE_COUNT -> calculateChangeCount(psiElement)
            PsiContextVariable.LINE_COUNT -> calculateLineCount(psiElement)
            PsiContextVariable.COMPLEXITY_COUNT -> calculateComplexityCount(psiElement)
        } ?: ""
    }

    private fun calculateChangeCount(psiElement: PsiElement?): String {
        // Placeholder implementation for change count
        return "0"
    }

    private fun calculateLineCount(psiElement: PsiElement?): String {
        // Placeholder implementation for line count
        return psiElement?.containingFile?.text?.lines()?.size.toString()
    }

    private fun calculateComplexityCount(psiElement: PsiElement?): String {
        // Placeholder implementation for complexity count
        return "0"
    }

    private fun toTestFileName(underTestFileName: String): String = underTestFileName + "_test.go"

    private fun getElementForTests(elementAtCaret: PsiElement): PsiElement? {
        val parent = PsiTreeUtil.getParentOfType(elementAtCaret, GoFunctionOrMethodDeclaration::class.java, false)
        if (parent == null) {
            val goFile: GoFile = elementAtCaret as? GoFile ?: return null
            return if (goFile.functions.isNotEmpty() || goFile.methods.isNotEmpty()) {
                goFile
            } else {
                null
            }
        }

        val virtualFile = elementAtCaret.containingFile?.virtualFile ?: return null

        val project = elementAtCaret.project
        if (TestSourcesFilter.isTestSources(virtualFile, project) || TestFinderHelper.isTest(parent)) return null

        return parent
    }
}
