package com.phodal.shirelang.java.variable

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.testIntegration.TestFinderHelper
import com.phodal.shirecore.provider.variable.impl.CodeSmellBuilder
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable.*
import com.phodal.shirecore.search.similar.SimilarChunksSearch
import com.phodal.shirelang.java.codemodel.JavaClassStructureProvider
import com.phodal.shirelang.java.util.JavaTestHelper
import com.phodal.shirelang.java.util.getContainingClass
import com.phodal.shirelang.java.variable.provider.JavaRelatedClassesProvider

class JavaPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        if (psiElement?.language?.id != "JAVA") return ""

        val clazz: PsiClass? = psiElement as? PsiClass ?: psiElement.getContainingClass()
        val sourceFile: PsiJavaFile = psiElement.containingFile as PsiJavaFile

        return when (variable) {
            IMPORTS -> sourceFile.importList?.text ?: ""
            CURRENT_CLASS_NAME -> clazz?.name ?: ""
            CURRENT_CLASS_CODE -> sourceFile.text
            CURRENT_METHOD_NAME -> (psiElement as? PsiMethod)?.name ?: ""
            CURRENT_METHOD_CODE -> (psiElement as? PsiMethod)?.body?.text ?: ""
            RELATED_CLASSES -> JavaRelatedClassesProvider().lookup(psiElement.parent).joinToString("\n") { it.text }
            SIMILAR_TEST_CASE -> JavaTestHelper.searchSimilarTestCases(psiElement).joinToString("\n") { it.text }
            IS_NEED_CREATE_FILE -> TestFinderHelper.findClassesForTest(psiElement).isEmpty()
            TARGET_TEST_FILE_NAME -> sourceFile.name.replace(".java", "") + "Test.java"
            UNDER_TEST_METHOD_CODE -> JavaTestHelper.extractMethodCalls(project, psiElement)
            CODE_SMELL -> CodeSmellBuilder.collectElementProblemAsSting(psiElement, project, editor)
            METHOD_CALLER -> {
                if (psiElement !is PsiMethod) return ""
                return JavaTestHelper.findCallers(psiElement).joinToString("\n") { it.text }
            }

            CALLED_METHOD -> {
                if (psiElement !is PsiMethod) return ""
                return JavaTestHelper.findCallees(psiElement).joinToString("\n") { it.text }
            }

            SIMILAR_CODE -> return SimilarChunksSearch.createQuery(psiElement) ?: ""
            STRUCTURE -> clazz?.let {
                JavaClassStructureProvider().build(it, true)?.toString() ?: ""
            } ?: ""

            FRAMEWORK_CONTEXT -> return collectFrameworkContext(psiElement, project)
            PsiContextVariable.CHANGE_COUNT -> calculateChangeCount(psiElement)
            PsiContextVariable.LINE_COUNT -> calculateLineCount(psiElement)
            PsiContextVariable.COMPLEXITY_COUNT -> calculateComplexityCount(psiElement)
        }
    }
}

