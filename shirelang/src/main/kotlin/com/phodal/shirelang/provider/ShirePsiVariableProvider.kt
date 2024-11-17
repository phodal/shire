package com.phodal.shirelang.provider

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirelang.ShireLanguage

class ShirePsiVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        if (psiElement == null) return ""
        if (psiElement.language != ShireLanguage.INSTANCE) return ""

        return when (variable) {
            PsiContextVariable.CURRENT_CLASS_NAME -> ""
            PsiContextVariable.CURRENT_CLASS_CODE -> ""
            PsiContextVariable.CURRENT_METHOD_NAME -> ""
            PsiContextVariable.CURRENT_METHOD_CODE -> ""
            PsiContextVariable.RELATED_CLASSES -> ""
            PsiContextVariable.SIMILAR_TEST_CASE -> ""
            PsiContextVariable.IMPORTS -> ""
            PsiContextVariable.IS_NEED_CREATE_FILE -> ""
            PsiContextVariable.TARGET_TEST_FILE_NAME -> ""
            PsiContextVariable.UNDER_TEST_METHOD_CODE -> ""
            PsiContextVariable.FRAMEWORK_CONTEXT -> {
                collectFrameworkContext(psiElement, project)
            }
            PsiContextVariable.CODE_SMELL -> ""
            PsiContextVariable.METHOD_CALLER -> ""
            PsiContextVariable.CALLED_METHOD -> ""
            PsiContextVariable.SIMILAR_CODE -> ""
            PsiContextVariable.STRUCTURE -> ""
            PsiContextVariable.CHANGE_COUNT -> calculateChangeCount(psiElement)
            PsiContextVariable.LINE_COUNT -> calculateLineCount(psiElement)
            PsiContextVariable.COMPLEXITY_COUNT -> ""
        }
    }
}
