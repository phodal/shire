package com.phodal.shire.database

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.ToolchainVariableProvider
import com.phodal.shirecore.provider.variable.model.ToolchainVariable
import com.phodal.shirecore.provider.variable.model.toolchain.DatabaseToolchainVariable

class DatabaseVariableProvider : ToolchainVariableProvider {
    override fun isResolvable(variable: ToolchainVariable, psiElement: PsiElement?): Boolean {
//        return psiElement?.language is SqlLanguageDialect || psiElement?.language is SqlLanguage
        return variable is DatabaseToolchainVariable
    }

    override fun resolve(variable: ToolchainVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
       return when (variable) {
            DatabaseToolchainVariable.Databases -> "databases"
            DatabaseToolchainVariable.Tables -> "tables"
            DatabaseToolchainVariable.Columns -> "columns"
            else -> ""
        }
    }
}
