package com.phodal.shire.database

import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.model.RawDataSource
import com.intellij.database.psi.DbDataSource
import com.intellij.database.psi.DbPsiFacade
import com.intellij.database.util.DasUtil
import com.intellij.openapi.project.Project

object DatabaseSchemaAssistant {
    fun getDataSources(project: Project): List<DbDataSource> {
        return DbPsiFacade.getInstance(project).dataSources.toList()
    }

    private fun retrieveRawDataSources(project: Project): List<RawDataSource> {
        val dbPsiFacade = DbPsiFacade.getInstance(project)
        return dbPsiFacade.dataSources.map { dataSource ->
            dbPsiFacade.getDataSourceManager(dataSource).dataSources
        }.flatten()
    }

    fun getDatabase(project: Project, dbName: String): RawDataSource {
        return retrieveRawDataSources(project).first { it.name == dbName }
    }

    fun getTables(project: Project): List<DasTable> {
        val rawDataSource = retrieveRawDataSources(project)
        val dasTables = rawDataSource.map { rawDataSource ->
            val schemaName = rawDataSource.name.substringBeforeLast('@')
            DasUtil.getTables(rawDataSource).filter { table ->
                table.kind == ObjectKind.TABLE && (table.dasParent?.name == schemaName || isSQLiteTable(rawDataSource, table))
            }
        }.flatten()

        return dasTables
    }

    fun getTable(dataSource: RawDataSource, tableName: String): List<DasTable> {
        val dasTables = DasUtil.getTables(dataSource)
        return dasTables.filter { it.name == tableName }.toList()
    }

    private fun isSQLiteTable(
        rawDataSource: RawDataSource,
        table: DasTable,
    ) = (rawDataSource.databaseVersion.name == "SQLite" && table.dasParent?.name == "main")

    fun getTableColumns(project: Project, tables: List<String> = emptyList()): List<String> {
        val dasTables = getTables(project)

        if (tables.isEmpty()) {
            return dasTables.map { table ->
                val dasColumns = DasUtil.getColumns(table)
                val columns = dasColumns.map { column ->
                    "${column.name}: ${column.dasType.toDataType()}"
                }.joinToString(", ")

                "TableName: ${table.name}, Columns: $columns"
            }
        }

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

    fun getTableColumn(table: DasTable): List<String> {
        val dasColumns = DasUtil.getColumns(table)
        return dasColumns.map { column ->
            "${column.name}: ${column.dasType.toDataType()}"
        }.toList()
    }
}