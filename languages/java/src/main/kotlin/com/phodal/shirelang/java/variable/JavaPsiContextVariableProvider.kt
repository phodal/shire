package com.phodal.shirelang.java.variable

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testIntegration.TestFinderHelper
import com.phodal.shirecore.provider.variable.PsiContextVariable
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.context.ToolchainPrepareContext
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirelang.java.variable.provider.JavaRelatedClassesProvider
import kotlinx.coroutines.runBlocking

class JavaPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolveVariableValue(psiElement: PsiElement?, variable: PsiContextVariable): Any {
        val project = psiElement?.project ?: return ""
        if (psiElement.language.id != "JAVA") return ""

        val clazz: PsiClass? = psiElement.getContainingClass()
        val sourceFile: PsiJavaFile = psiElement.containingFile as PsiJavaFile

        return when (variable) {
            PsiContextVariable.CURRENT_CLASS_NAME -> {
                clazz?.name ?: ""
            }

            PsiContextVariable.CURRENT_CLASS_CODE -> {
                sourceFile.text
            }

            PsiContextVariable.CURRENT_METHOD_NAME -> {
                (psiElement as? PsiMethod)?.name ?: ""
            }

            PsiContextVariable.CURRENT_METHOD_CODE -> {
                (psiElement as? PsiMethod)?.body?.text ?: ""
            }

            PsiContextVariable.RELATED_CLASSES -> {
                JavaRelatedClassesProvider().lookup(psiElement.parent).joinToString("\n") { it.text }
            }

            PsiContextVariable.SIMILAR_TEST_CASE -> {
                return listOf<String>()
            }

            PsiContextVariable.IMPORTS -> {
                sourceFile.importList?.text ?: ""
            }

            PsiContextVariable.IS_NEED_CREATE_FILE -> {
                return TestFinderHelper.findClassesForTest(psiElement).isEmpty()
            }

            PsiContextVariable.TARGET_TEST_FILE_NAME -> {
                val testFileName = sourceFile.name.replace(".java", "") + "Test"
                "$testFileName.java"
            }

            PsiContextVariable.UNDER_TEST_METHOD_CODE -> {
                return lookupUnderTestMethod(project, psiElement)
            }

            PsiContextVariable.FRAMEWORK_CONTEXT -> {
                runBlocking {
                    val prepareContext = ToolchainPrepareContext(sourceFile, psiElement)
                    val contextItems =
                        LanguageToolchainProvider.collectToolchainContext(project, prepareContext)

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

fun PsiElement.getContainingClass(): PsiClass? {
    var context: PsiElement? = this.context
    while (context != null) {
        if (context is PsiClass) return context
        if (context is PsiMember) return context.containingClass

        context = context.context
    }

    return null
}

fun PsiElement.getContainingMethod(): PsiMethod? {
    var context: PsiElement? = this.context
    while (context != null) {
        if (context is PsiMethod) return context

        context = context.context
    }

    return null
}
