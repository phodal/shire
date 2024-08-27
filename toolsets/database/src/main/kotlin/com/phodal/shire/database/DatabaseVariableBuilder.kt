package com.phodal.shire.database

import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.model.RawDataSource
import com.intellij.database.psi.DbPsiFacade
import com.intellij.database.util.DasUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.sql.dialects.sqlite.SqliteDialect

object DatabaseVariableBuilder {
    fun getTables(project: Project, file: PsiFile): List<DasTable> {
        val rawDataSource = retrieveFirstRawDataSource(project) ?: return emptyList()
        val schemaName = rawDataSource.name.substringBeforeLast('@')
        val dasTables = rawDataSource.let {
            val tables = DasUtil.getTables(it)
            tables.filter { table -> table.kind == ObjectKind.TABLE &&
                    (table.dasParent?.name == schemaName || (file.language == SqliteDialect.INSTANCE && table.dasParent?.name == "main"))
            }.toList()
        }.toList()

        return dasTables
    }

    fun getTableColumns(dasTables: List<DasTable>, tables: List<String>): List<String> {
        return dasTables.mapNotNull { tableName ->
            if (tables.contains(tableName.name)) {
                val dasColumns = DasUtil.getColumns(tableName)
                val columns = dasColumns.map {
                    "${it.name}: ${it.dasType.toDataType()}"
                }.joinToString(", ")

                "TableName: ${tableName.name}, Columns: $columns"
            } else {
                null
            }
        }
    }

    private fun retrieveFirstRawDataSource(project: Project): RawDataSource? {
        val dbPsiFacade = DbPsiFacade.getInstance(project)
        val dataSource = dbPsiFacade.dataSources.firstOrNull() ?: return null
        val rawDataSource = dbPsiFacade.getDataSourceManager(dataSource).dataSources.firstOrNull() ?: return null
        return rawDataSource
    }
}