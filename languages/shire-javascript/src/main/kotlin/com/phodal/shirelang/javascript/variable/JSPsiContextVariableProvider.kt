package com.phodal.shirelang.javascript.variable

import com.intellij.lang.ecmascript6.psi.ES6ImportDeclaration
import com.intellij.lang.ecmascript6.psi.ES6ImportSpecifier
import com.intellij.lang.ecmascript6.psi.ES6ImportSpecifierAlias
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.lang.javascript.psi.ecmal4.JSImportStatement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.impl.CodeSmellBuilder
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirecore.provider.variable.model.PsiContextVariable.*
import com.phodal.shirecore.search.similar.SimilarChunksSearch
import com.phodal.shirelang.javascript.codemodel.JavaScriptClassStructureProvider
import com.phodal.shirelang.javascript.codemodel.JavaScriptMethodStructureProvider
import com.phodal.shirelang.javascript.util.JSPsiUtil
import com.phodal.shirelang.javascript.util.JSRelevantUtil

class JSPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        if (psiElement == null) return ""
        if(!psiElement.language.isKindOf(JavascriptLanguage.INSTANCE)) return ""

        val underTestElement = JSPsiUtil.getElementToTest(psiElement) ?: return ""
        val sourceFile = underTestElement.containingFile as? JSFile ?: return ""

        return when (variable) {
            CURRENT_CLASS_NAME -> {
                when (underTestElement) {
                    is JSClass -> underTestElement.name ?: ""
                    else -> ""
                }
            }

            CURRENT_CLASS_CODE -> {
                val underTestObj = JavaScriptClassStructureProvider()
                    .build(underTestElement, false)?.format()

                if (underTestObj == null) {
                    val funcObj = JavaScriptMethodStructureProvider()
                        .build(underTestElement, false, false)?.format()

                    funcObj ?: ""
                } else {
                    underTestObj
                }
            }

            CURRENT_METHOD_NAME -> {
                when (underTestElement) {
                    is JSFunction -> underTestElement.name ?: ""
                    else -> ""
                }
            }

            CURRENT_METHOD_CODE -> {
                when (underTestElement) {
                    is JSFunction -> underTestElement.text ?: ""
                    else -> ""
                }
            }

            RELATED_CLASSES -> JSRelevantUtil.lookupRelevantClass(underTestElement)
            SIMILAR_TEST_CASE -> ""
            IMPORTS -> {
                return PsiTreeUtil.findChildrenOfAnyType(sourceFile,
                    JSImportStatement::class.java,
                    ES6ImportDeclaration::class.java,
                    ES6ImportSpecifier::class.java,
                    ES6ImportSpecifierAlias::class.java
                )
                    .map { it.text }
            }

            IS_NEED_CREATE_FILE -> TODO()
            TARGET_TEST_FILE_NAME -> JSPsiUtil.getTestFilePath(psiElement) ?: ""
            UNDER_TEST_METHOD_CODE -> {
                when (underTestElement) {
                    is JSFunction -> underTestElement.text ?: ""
                    else -> ""
                }
            }

            FRAMEWORK_CONTEXT -> {
                collectFrameworkContext(underTestElement, project)
            }

            CODE_SMELL -> CodeSmellBuilder.collectElementProblemAsSting(psiElement, project, editor)
            METHOD_CALLER -> TODO()
            CALLED_METHOD -> TODO()
            SIMILAR_CODE -> return SimilarChunksSearch.createQuery(psiElement) ?: ""
            STRUCTURE -> {
                when (underTestElement) {
                    is JSClass -> JavaScriptClassStructureProvider().build(underTestElement, true)?.toString() ?: ""
                    is JSFunction -> JavaScriptMethodStructureProvider().build(underTestElement, false, false)
                    else -> null
                } ?: ""
            }
            PsiContextVariable.CHANGE_COUNT -> calculateChangeCount(psiElement)
            PsiContextVariable.LINE_COUNT -> calculateLineCount(psiElement)
            PsiContextVariable.COMPLEXITY_COUNT -> ""
        }
    }

}
