package com.phodal.shire.database.provider

import com.intellij.database.util.DbUtil
import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainContextItem
import com.phodal.shirecore.provider.context.ToolchainPrepareContext

class DatabaseToolchainProvider : LanguageToolchainProvider {
    override fun isApplicable(project: Project, context: ToolchainPrepareContext): Boolean {
        return return DbUtil.getDataSources(project).isNotEmpty
    }

    override suspend fun collect(project: Project, context: ToolchainPrepareContext): List<ToolchainContextItem> {
        val dataSources = DbUtil.getDataSources(project)
        if (dataSources.isEmpty) return emptyList()

        val infos = dataSources.mapNotNull {
            val dbNames = it.delegateDataSource?.databaseVersion ?: return@mapNotNull null
            val nameInfo = dbNames.name + " " + dbNames.version
            val text = "This project use $nameInfo"
            return@mapNotNull ToolchainContextItem(DatabaseToolchainProvider::class, text)
        }.toList()

        return infos
    }
}
