package com.phodal.shire.database

import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.model.RawDataSource
import com.intellij.database.psi.DbDataSource
import com.intellij.database.psi.DbPsiFacade
import com.intellij.database.util.DasUtil
import com.intellij.openapi.project.Project

object DatabaseVariableBuilder {
    fun getDatabases(project: Project): List<DbDataSource> {
        val dbPsiFacade = DbPsiFacade.getInstance(project)
        return dbPsiFacade.dataSources.toList()
    }

    fun getTables(project: Project): List<DasTable> {
        val rawDataSource = retrieveFirstRawDataSource(project) ?: return emptyList()
        val schemaName = rawDataSource.name.substringBeforeLast('@')
        val dasTables = rawDataSource.let {
            DasUtil.getTables(it).filter { table ->
                table.kind == ObjectKind.TABLE && (table.dasParent?.name == schemaName)
            }.toList()
        }.toList()

        return dasTables
    }

    fun getTableColumns(project: Project, tables: List<String> = emptyList()): List<String> {
        val dasTables = getTables(project)

        if (dasTables.isEmpty()) {
            return dasTables.map {
                val dasColumns = DasUtil.getColumns(it)
                val columns = dasColumns.map {
                    "${it.name}: ${it.dasType.toDataType()}"
                }.joinToString(", ")

                "TableName: ${it.name}, Columns: $columns"
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

    private fun retrieveFirstRawDataSource(project: Project): RawDataSource? {
        val dbPsiFacade = DbPsiFacade.getInstance(project)
        val dataSource = dbPsiFacade.dataSources.firstOrNull() ?: return null
        val rawDataSource = dbPsiFacade.getDataSourceManager(dataSource).dataSources.firstOrNull() ?: return null
        return rawDataSource
    }
}