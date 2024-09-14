package com.phodal.shirelang.javascript.variable

import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.ecmal4.JSImportStatement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.oracle.truffle.js.runtime.builtins.JSClass
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirelang.javascript.codemodel.JavaScriptClassStructureProvider
import com.phodal.shirelang.javascript.codemodel.JavaScriptMethodStructureProvider
import com.phodal.shirelang.javascript.util.JSPsiUtil
import com.phodal.shirelang.javascript.util.JSRelevantUtil

class JSPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        if (psiElement?.language !is JavascriptLanguage) return ""
        val underTestElement = JSPsiUtil.getElementToTest(psiElement) ?: return ""
        val sourceFile = underTestElement.containingFile as? JSFile ?: return ""

        return when (variable) {
            PsiContextVariable.CURRENT_CLASS_NAME -> {
//                when (underTestElement) {
//                    is JSClass -> underTestElement.getName() ?: ""
//                    else -> ""
//                }
                ""
            }

            PsiContextVariable.CURRENT_CLASS_CODE -> {
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

            PsiContextVariable.CURRENT_METHOD_NAME -> {
                when (underTestElement) {
                    is JSFunction -> underTestElement.name ?: ""
                    else -> ""
                }
            }

            PsiContextVariable.CURRENT_METHOD_CODE -> {
                when (underTestElement) {
                    is JSFunction -> underTestElement.text ?: ""
                    else -> ""
                }
            }

            PsiContextVariable.RELATED_CLASSES -> JSRelevantUtil.lookupRelevantClass(underTestElement)
            PsiContextVariable.SIMILAR_TEST_CASE -> ""
            PsiContextVariable.IMPORTS -> PsiTreeUtil.findChildrenOfType(sourceFile, JSImportStatement::class.java)
                .map { it.text }

            PsiContextVariable.IS_NEED_CREATE_FILE -> TODO()
            PsiContextVariable.TARGET_TEST_FILE_NAME -> JSPsiUtil.getTestFilePath(psiElement) ?: ""
            PsiContextVariable.UNDER_TEST_METHOD_CODE -> TODO()
            PsiContextVariable.FRAMEWORK_CONTEXT -> TODO()
            PsiContextVariable.CODE_SMELL -> TODO()
            PsiContextVariable.METHOD_CALLER -> TODO()
            PsiContextVariable.CALLED_METHOD -> TODO()
            PsiContextVariable.SIMILAR_CODE -> TODO()
            PsiContextVariable.STRUCTURE -> TODO()
        }
    }

}
