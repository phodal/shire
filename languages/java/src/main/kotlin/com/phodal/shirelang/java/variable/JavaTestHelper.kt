package com.phodal.shirelang.java.variable

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.search.searches.MethodReferencesSearch
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.*
import com.phodal.shirecore.search.CodeNamingTokenizer
import com.phodal.shirecore.search.TfIdf
import java.util.function.Consumer


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

    fun searchSimilarTestCases(psiElement: PsiElement, minScore: Double = 1.0): List<PsiMethod> {
        val project = psiElement.project
        val psiMethod = psiElement as? PsiMethod ?: return emptyList()
        val methodName = psiMethod.name

        // 使用缓存机制获取所有测试方法
        val allTestMethods = getAllTestMethods(project)

        // 使用 TfIdf 进行相似度计算
        val tfIdf = TfIdf<String, List<PsiMethod>>()
        tfIdf.setTokenizer(CodeNamingTokenizer())

        allTestMethods.forEach { tfIdf.addDocument(it.name, it) }

        return tfIdf.tfidfs(methodName)
            .mapIndexedNotNull { index, measure ->
                if (measure > minScore) allTestMethods[index] else null
            }
    }

    /**
     * Retrieves all test methods from the given project.
     *
     * @param project the project from which to retrieve the test methods
     * @return a list of PsiMethod objects representing all test methods found in the project
     */
    private fun getAllTestMethods(project: Project): List<PsiMethod> {
        val cachedValue: CachedValue<List<PsiMethod>> = CachedValuesManager.getManager(project).createCachedValue {
            val testMethods = mutableListOf<PsiMethod>()
            val scope = GlobalSearchScope.projectScope(project)

            PsiShortNamesCache.getInstance(project).allClassNames
                .filter { it.contains("Test") }
                .forEach { className ->
                    PsiShortNamesCache.getInstance(project).getClassesByName(className, scope)
                        .filter { it.containingFile.name.endsWith("Test.java") }
                        .forEach { psiClass ->
                            testMethods.addAll(psiClass.methods)
                        }
                }

            CachedValueProvider.Result.create(testMethods, PsiModificationTracker.MODIFICATION_COUNT)
        }

        return cachedValue.value
    }

    /**
     * Finds all the callers of a given method.
     *
     * @param method the method for which callers need to be found
     * @return a list of PsiMethod objects representing the callers of the given method
     */
    fun findCallers(method: PsiMethod): List<PsiMethod> {
        val callers: MutableList<PsiMethod> = ArrayList()

        val references = ReferencesSearch.search(method).findAll()

        for (reference in references) {
            val element = reference.element

            if (element is PsiMethodCallExpression) {
                val callerMethod = element.resolveMethod()
                if (callerMethod != null) {
                    callers.add(callerMethod)
                }
            }
        }

        return callers
    }

    /**
     * Finds all the methods called by the given method.
     *
     * @param method the method for which callees need to be found
     * @return a list of PsiMethod objects representing the methods called by the given method
     */
    fun findCallees(method: PsiMethod): List<PsiMethod> {
        val callees: MutableList<PsiMethod> = ArrayList()

        MethodReferencesSearch.search(method).forEach(Consumer { reference: PsiReference ->
            val element = reference.element
            if (element is PsiMethodCallExpression) {
                val resolvedMethod = element.resolveMethod()
                if (resolvedMethod != null) {
                    callees.add(resolvedMethod)
                }
            }
        })

        return callees
    }
}
