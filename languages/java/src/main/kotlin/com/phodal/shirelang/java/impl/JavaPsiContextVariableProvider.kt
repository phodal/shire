package com.phodal.shirelang.java.impl

import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testIntegration.TestFinderHelper
import com.phodal.shirecore.provider.PsiVariable
import com.phodal.shirecore.provider.PsiContextVariableProvider
import com.phodal.shirecore.provider.ToolchainPrepareContext
import com.phodal.shirecore.provider.ToolchainProvider
import com.phodal.shirelang.java.toolchain.getContainingClass
import kotlinx.coroutines.runBlocking

class JavaPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolveVariableValue(psiElement: PsiElement?, variable: PsiVariable): String {
        val project = psiElement?.project ?: return ""
        if (psiElement.language.id != "JAVA") {
            return ""
        }

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
                ""
            }

            PsiVariable.SIMILAR_TEST_CASE -> {
                ""
            }

            PsiVariable.IMPORTS -> {
                sourceFile.importList?.text ?: ""
            }

            PsiVariable.IS_NEW_FILE -> {
                val sourceElement = TestFinderHelper.findClassesForTest(psiElement)
                return if (sourceElement.isEmpty()) {
                    "true"
                } else {
                    "false"
                }
            }

            PsiVariable.TARGET_TEST_FILE_NAME -> {
                val testFileName = sourceFile.name.replace(".java", "") + "Test"
                "$testFileName.java"
            }

            PsiVariable.UNDER_TEST_METHOD_CODE -> {
                val searchScope = GlobalSearchScope.allScope(project)

                when (psiElement) {
                    is PsiMethod -> {
                        val calls: List<PsiMethodCallExpression> =
                            PsiTreeUtil.findChildrenOfAnyType(psiElement.body, PsiMethodCallExpression::class.java)
                                .toList()

                        val strings = calls
                            .mapNotNull { it ->
                                it.methodExpression.resolve()?.let {
                                    it as PsiMethod
                                }
                            }
                            .filter {
                                if (it.containingClass == null) return@filter false

                                val isEmpty = ClassInheritorsSearch.search(it.containingClass!!, searchScope, true)
                                    .findAll().isEmpty()

                                !isEmpty
                            }
                            .map {
                                it.text
                            }

                        return strings.joinToString("\n")
                    }
                }

                return ""
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

}
