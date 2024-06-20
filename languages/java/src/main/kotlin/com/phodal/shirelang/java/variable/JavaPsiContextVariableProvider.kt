package com.phodal.shirelang.java.variable

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testIntegration.TestFinderHelper
import com.phodal.shirecore.provider.variable.PsiVariable
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.context.ToolchainPrepareContext
import com.phodal.shirecore.provider.context.ToolchainProvider
import com.phodal.shirelang.java.toolchain.getContainingClass
import com.phodal.shirelang.java.variable.provider.JavaRelatedClassesProvider
import kotlinx.coroutines.runBlocking

class JavaPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolveVariableValue(psiElement: PsiElement?, variable: PsiVariable): Any {
        val project = psiElement?.project ?: return ""
        if (psiElement.language.id != "JAVA") return ""

        val clazz: PsiClass? = psiElement.getContainingClass()
        val sourceFile: PsiJavaFile = psiElement.containingFile as PsiJavaFile

        return when (variable) {
            PsiVariable.CURRENT_CLASS_NAME -> {
                clazz?.name ?: ""
            }

            PsiVariable.CURRENT_CLASS_CODE -> {
                sourceFile.text
            }

            PsiVariable.RELATED_CLASSES -> {
                JavaRelatedClassesProvider().lookup(psiElement.parent).joinToString("\n") { it.text }
            }

            PsiVariable.SIMILAR_TEST_CASE -> {
                return listOf<String>()
            }

            PsiVariable.IMPORTS -> {
                sourceFile.importList?.text ?: ""
            }

            PsiVariable.IS_NEW_FILE -> {
                val sourceElement = TestFinderHelper.findClassesForTest(psiElement)
                return sourceElement.isEmpty()
            }

            PsiVariable.TARGET_TEST_FILE_NAME -> {
                val testFileName = sourceFile.name.replace(".java", "") + "Test"
                "$testFileName.java"
            }

            PsiVariable.UNDER_TEST_METHOD_CODE -> {
                return lookupUnderTestMethod(project, psiElement)
            }

            PsiVariable.FRAMEWORK_CONTEXT -> {
                runBlocking {
                    val prepareContext = ToolchainPrepareContext(sourceFile, psiElement)
                    val contextItems =
                        ToolchainProvider.gatherToolchainContextItems(project, prepareContext)

                    contextItems.joinToString("\n") { it.text }
                }
            }
        }
    }

    private fun lookupUnderTestMethod(project: Project, psiElement: PsiElement): String {
        val searchScope = GlobalSearchScope.allScope(project)

        return when (psiElement) {
            is PsiMethod -> {
                val calls = PsiTreeUtil
                    .findChildrenOfAnyType(psiElement.body, PsiMethodCallExpression::class.java)
                    .toList()

                return calls
                    .mapNotNull(PsiMethodCallExpression::resolveMethod)
                    .filter {
                        if (it.containingClass == null) return@filter false

                        ClassInheritorsSearch
                            .search(it.containingClass!!, searchScope, true)
                            .findAll().isNotEmpty()
                    }.joinToString("\n") {
                        it.text
                    }
            }

            else -> ""
        }
    }

}
