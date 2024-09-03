package com.phodal.shirecore.provider.variable.impl

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirecore.provider.variable.model.PsiContextVariable.FRAMEWORK_CONTEXT

class DefaultPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): String {
        return when (variable) {
            FRAMEWORK_CONTEXT -> {
                return collectFrameworkContext(psiElement, project)
            }

            else -> ""
        }
    }
}