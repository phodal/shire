package com.phodal.shirelang.java.variable

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.util.*
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

    fun getAllTestMethods(project: Project): List<PsiMethod> {
        val cachedValue: CachedValue<List<PsiMethod>> = CachedValuesManager.getManager(project).createCachedValue {
            val testMethods = mutableListOf<PsiMethod>()
            val scope = GlobalSearchScope.projectScope(project)

            // 获取所有文件名中包含 'Test' 的类
            PsiShortNamesCache.getInstance(project).getAllClassNames()
                .filter { it.contains("Test") }
                .forEach { className ->
                    // 获取所有匹配的类
                    PsiShortNamesCache.getInstance(project).getClassesByName(className, scope)
                        .filter { it.containingFile.name.endsWith("Test.java") }
                        .forEach { psiClass ->
                            // 收集所有方法
                            testMethods.addAll(psiClass.methods)
                        }
                }

            CachedValueProvider.Result.create(testMethods, PsiModificationTracker.MODIFICATION_COUNT)
        }

        return cachedValue.value
    }}
