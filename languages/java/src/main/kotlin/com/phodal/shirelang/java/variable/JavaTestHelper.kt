package com.phodal.shirelang.java.variable

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.util.PsiTreeUtil
import com.phodal.shirecore.search.CodeNamingTokenizer
import com.phodal.shirecore.search.TfIdf

object JavaTestHelper {
    fun lookupUnderTestMethod(project: Project, psiElement: PsiElement): String {
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

    fun findSimilarTestCases(psiElement: PsiElement, minScore: Double = 1.0): List<PsiMethod> {
        val psiMethod = psiElement as? PsiMethod ?: return emptyList()

        val methodName = psiMethod.name

        val allTestFile = psiElement.containingFile.containingDirectory.files
            .filter { it.name.contains("Test") }
            .map { it as PsiJavaFile }

        val allTestMethod: List<PsiMethod> = allTestFile.flatMap { javaFile ->
            javaFile.classes.flatMap { it.methods.toList() }
        }

        val tfIdf = TfIdf<String, List<PsiMethod>>()
        tfIdf.setTokenizer(CodeNamingTokenizer())

        allTestMethod.forEach {
            tfIdf.addDocument(it.name, it)
        }

        val results : MutableList<PsiMethod> = mutableListOf()
        val scores = tfIdf.tfidfs(methodName)

        scores.forEachIndexed { index, measure ->
            if (measure > minScore) {
                results += allTestMethod[index]
            }
        }

        return results
    }

}
