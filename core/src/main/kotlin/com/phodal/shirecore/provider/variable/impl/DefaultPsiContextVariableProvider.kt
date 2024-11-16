package com.phodal.shirecore.provider.variable.impl

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirecore.provider.variable.model.PsiContextVariable.FRAMEWORK_CONTEXT
import com.phodal.shirecore.provider.variable.model.PsiContextVariable.CHANGE_COUNT
import com.phodal.shirecore.provider.variable.model.PsiContextVariable.LINE_COUNT
import com.phodal.shirecore.provider.variable.model.PsiContextVariable.COMPLEXITY_COUNT

class DefaultPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): String {
        return when (variable) {
            FRAMEWORK_CONTEXT -> {
                return collectFrameworkContext(psiElement, project)
            }
            CHANGE_COUNT -> {
                // Implement logic to calculate change count
                return "0" // Placeholder value
            }
            LINE_COUNT -> {
                // Implement logic to calculate line count
                return "0" // Placeholder value
            }
            COMPLEXITY_COUNT -> {
                // Implement logic to calculate complexity count
                return "0" // Placeholder value
            }
            else -> ""
        }
    }
}
