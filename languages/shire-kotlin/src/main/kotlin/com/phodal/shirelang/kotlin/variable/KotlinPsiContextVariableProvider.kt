package com.phodal.shirelang.kotlin.variable

import org.jetbrains.kotlin.idea.KotlinLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testIntegration.TestFinderHelper
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirecore.psi.CodeSmellCollector
import com.phodal.shirelang.kotlin.codemodel.KotlinClassStructureProvider
import com.phodal.shirelang.kotlin.provider.KotlinRelatedClassesProvider
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtNamedFunction

class KotlinPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        if (psiElement?.language != KotlinLanguage.INSTANCE) return ""

        val psiFile = psiElement.containingFile
        val importList = PsiTreeUtil.getChildrenOfTypeAsList(psiFile, KtImportList::class.java)

        return when (variable) {
            PsiContextVariable.CURRENT_CLASS_NAME -> {
                val clazz: KtClassOrObject = PsiTreeUtil.getParentOfType(psiElement, KtClassOrObject::class.java) ?: return ""
                clazz.name ?: ""
            }
            PsiContextVariable.CURRENT_CLASS_CODE -> {
                val clazz: KtClassOrObject = PsiTreeUtil.getParentOfType(psiElement, KtClassOrObject::class.java) ?: return ""
                clazz.text
            }
            PsiContextVariable.CURRENT_METHOD_NAME -> {
                when (psiElement) {
                    is KtClassOrObject -> psiElement.name ?: ""
                    is KtNamedFunction -> psiElement.name ?: ""
                    else -> psiElement.text
                }
            }
            PsiContextVariable.CURRENT_METHOD_CODE -> {
                when (psiElement) {
                    is KtClassOrObject -> psiElement.text ?: ""
                    is KtNamedFunction -> psiElement.bodyExpression?.text ?: ""
                    else -> psiElement.text
                }
            }
            PsiContextVariable.RELATED_CLASSES -> {
                KotlinRelatedClassesProvider().lookup(psiElement.parent).joinToString("\n") { it.text }
            }
            PsiContextVariable.SIMILAR_TEST_CASE -> { "" }
            PsiContextVariable.IMPORTS -> {
                importList.joinToString("\n") { it.text }
            }
            PsiContextVariable.IS_NEED_CREATE_FILE -> TestFinderHelper.findClassesForTest(psiElement).isEmpty()
            PsiContextVariable.TARGET_TEST_FILE_NAME -> psiFile.name.replace(".kt", "") + "Test.kt"
            PsiContextVariable.UNDER_TEST_METHOD_CODE -> { "" }
            PsiContextVariable.FRAMEWORK_CONTEXT -> collectFrameworkContext(psiElement, project)
            PsiContextVariable.CODE_SMELL -> CodeSmellCollector.collectElementProblemAsSting(psiElement, project, editor)
            PsiContextVariable.METHOD_CALLER -> TODO()
            PsiContextVariable.CALLED_METHOD -> TODO()
            PsiContextVariable.SIMILAR_CODE -> TODO()
            PsiContextVariable.STRUCTURE -> when (psiElement) {
                is KtClassOrObject -> KotlinClassStructureProvider().build(psiElement, true)?.toString() ?: ""
                else -> ""
            }
            PsiContextVariable.CHANGE_COUNT -> calculateChangeCount(psiElement)
            PsiContextVariable.LINE_COUNT -> calculateLineCount(psiElement)
            PsiContextVariable.COMPLEXITY_COUNT -> calculateComplexityCount(psiElement)
        }
    }

}
