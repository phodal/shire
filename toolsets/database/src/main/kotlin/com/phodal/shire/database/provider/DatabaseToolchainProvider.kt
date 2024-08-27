package com.phodal.shire.database.provider

import com.intellij.openapi.project.Project
import com.intellij.sql.dialects.SqlLanguageDialect
import com.intellij.sql.psi.SqlFile
import com.intellij.sql.psi.SqlLanguage
import com.intellij.sql.psi.impl.SqlImplUtil
import com.phodal.shire.database.SqlContextBuilder
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainContextItem
import com.phodal.shirecore.provider.context.ToolchainPrepareContext

class DatabaseToolchainProvider : LanguageToolchainProvider {
    override fun isApplicable(project: Project, context: ToolchainPrepareContext): Boolean {
        val element = context.element ?: return false
        return element.language is SqlLanguageDialect || element.language is SqlLanguage
    }

    override suspend fun collect(project: Project, context: ToolchainPrepareContext): List<ToolchainContextItem> {
        val file = context.element?.containingFile as? SqlFile ?: return emptyList()
        val ds = SqlImplUtil.getDataSources(file).firstOrNull() ?: return emptyList()

        val currentNamespace = SqlContextBuilder.getCurrentNamespace(file) ?: return emptyList()
        val schema = SqlContextBuilder.getSchema(ds, currentNamespace) ?: return emptyList()

        val displayName: String = file.language.displayName
        val displayCode: String = context.element?.text ?: ""
        val schemaDescription: String? = SqlContextBuilder.formatSchema(schema)

        val text = if (schemaDescription == null) {
            "```$displayName\n\nConsider the following database schema:\n\n$displayCode\n\nNo schema description available\n```"
        } else {
            "```$displayName\n\nConsider the following database schema:\n$schemaDescription\n```"
        }

        return listOf(ToolchainContextItem(DatabaseToolchainProvider::class, text))
    }
}
