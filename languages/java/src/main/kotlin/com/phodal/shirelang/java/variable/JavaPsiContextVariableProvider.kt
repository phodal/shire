package com.phodal.shirelang.java.variable

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.testIntegration.TestFinderHelper
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainPrepareContext
import com.phodal.shirecore.provider.variable.impl.CodeSmellBuilder
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.search.SimilarChunksSearch
import com.phodal.shirelang.java.util.JavaTestHelper
import com.phodal.shirelang.java.util.getContainingClass
import com.phodal.shirelang.java.variable.provider.JavaRelatedClassesProvider
import kotlinx.coroutines.runBlocking

class JavaPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        if (psiElement?.language?.id != "JAVA") return ""

        val clazz: PsiClass? = psiElement.getContainingClass()
        val sourceFile: PsiJavaFile = psiElement.containingFile as PsiJavaFile

        return when (variable) {
            PsiContextVariable.IMPORTS -> sourceFile.importList?.text ?: ""

            PsiContextVariable.CURRENT_CLASS_NAME -> clazz?.name ?: ""

            PsiContextVariable.CURRENT_CLASS_CODE -> sourceFile.text

            PsiContextVariable.CURRENT_METHOD_NAME -> (psiElement as? PsiMethod)?.name ?: ""

            PsiContextVariable.CURRENT_METHOD_CODE -> (psiElement as? PsiMethod)?.body?.text ?: ""

            PsiContextVariable.RELATED_CLASSES -> {
                JavaRelatedClassesProvider().lookup(psiElement.parent).joinToString("\n") { it.text }
            }

            PsiContextVariable.SIMILAR_TEST_CASE -> {
                JavaTestHelper.searchSimilarTestCases(psiElement).joinToString("\n") { it.text }
            }

            PsiContextVariable.IS_NEED_CREATE_FILE -> TestFinderHelper.findClassesForTest(psiElement).isEmpty()

            PsiContextVariable.TARGET_TEST_FILE_NAME -> sourceFile.name.replace(".java", "") + "Test.java"

            PsiContextVariable.UNDER_TEST_METHOD_CODE -> {
                JavaTestHelper.extractMethodCalls(project, psiElement)
            }

            PsiContextVariable.FRAMEWORK_CONTEXT -> {
                runBlocking {
                    val prepareContext = ToolchainPrepareContext(sourceFile, psiElement)
                    val contextItems =
                        LanguageToolchainProvider.collectToolchainContext(project, prepareContext)

                    contextItems.joinToString("\n") { it.text }
                }
            }

            PsiContextVariable.CODE_SMELL -> CodeSmellBuilder.collectElementProblemAsSting(psiElement, project, editor)
            PsiContextVariable.METHOD_CALLER -> {
                if (psiElement !is PsiMethod) return ""
                return JavaTestHelper.findCallers(psiElement).joinToString("\n") { it.text }
            }
            PsiContextVariable.CALLED_METHOD -> {
                if (psiElement !is PsiMethod) return ""
                return JavaTestHelper.findCallees(psiElement).joinToString("\n") { it.text }
            }

            PsiContextVariable.SIMILAR_CODE -> return SimilarChunksSearch.createQuery(psiElement) ?: ""
        }
    }
}

