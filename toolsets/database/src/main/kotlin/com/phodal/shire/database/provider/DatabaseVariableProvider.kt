package com.phodal.shire.database.provider

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shire.database.DatabaseVariableBuilder
import com.phodal.shirecore.provider.variable.ToolchainVariableProvider
import com.phodal.shirecore.provider.variable.model.ToolchainVariable
import com.phodal.shirecore.provider.variable.model.toolchain.DatabaseToolchainVariable

class DatabaseVariableProvider : ToolchainVariableProvider {
    override fun isResolvable(variable: ToolchainVariable, psiElement: PsiElement?): Boolean {
        return variable is DatabaseToolchainVariable
    }

    override fun resolve(variable: ToolchainVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
       return when (variable) {
            DatabaseToolchainVariable.Databases -> DatabaseVariableBuilder.getDataSources(project)
            DatabaseToolchainVariable.Tables -> DatabaseVariableBuilder.getTables(project)
            DatabaseToolchainVariable.Columns -> DatabaseVariableBuilder.getTableColumns(project)
            else -> ""
        }
    }
}

