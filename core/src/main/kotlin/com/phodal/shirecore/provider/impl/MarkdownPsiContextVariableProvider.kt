package com.phodal.shirecore.provider.impl

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import org.intellij.markdown.parser.MarkdownParser

class MarkdownPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        if (psiElement?.language?.id?.lowercase() != "markdown") return ""

        return when (variable) {
            PsiContextVariable.STRUCTURE -> {
               ""
            }
            else -> {
                ""
            }
        }
    }
}