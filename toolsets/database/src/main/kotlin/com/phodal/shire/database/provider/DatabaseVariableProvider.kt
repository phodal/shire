package com.phodal.shire.database.provider

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shire.database.DatabaseSchemaAssistant
import com.phodal.shirecore.provider.variable.ToolchainVariableProvider
import com.phodal.shirecore.provider.variable.model.ToolchainVariable
import com.phodal.shirecore.provider.variable.model.toolchain.DatabaseToolchainVariable

class DatabaseVariableProvider : ToolchainVariableProvider {
    override fun isResolvable(variable: ToolchainVariable, psiElement: PsiElement?, project: Project): Boolean {
        return variable is DatabaseToolchainVariable
    }

    override fun resolve(variable: ToolchainVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
       return when (variable) {
            DatabaseToolchainVariable.Databases -> DatabaseSchemaAssistant.getDataSources(project)
            DatabaseToolchainVariable.Tables -> DatabaseSchemaAssistant.getAllTables(project)
            DatabaseToolchainVariable.Columns -> DatabaseSchemaAssistant.getTableColumns(project)
            else -> ""
        }
    }
}

