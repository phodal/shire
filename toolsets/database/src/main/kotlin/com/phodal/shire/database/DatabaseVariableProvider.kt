package com.phodal.shire.database

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.sql.dialects.SqlLanguageDialect
import com.intellij.sql.psi.SqlLanguage
import com.phodal.shirecore.provider.variable.ToolchainVariableProvider
import com.phodal.shirecore.provider.variable.model.ToolchainVariable

class DatabaseVariableProvider : ToolchainVariableProvider {
    override fun isResolvable(variable: ToolchainVariable, psiElement: PsiElement?): Boolean {
        return psiElement?.language is SqlLanguageDialect || psiElement?.language is SqlLanguage
    }

    override fun resolve(variable: ToolchainVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
       return ""
    }
}
