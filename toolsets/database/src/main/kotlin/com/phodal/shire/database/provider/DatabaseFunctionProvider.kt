package com.phodal.shire.database.provider

import com.intellij.database.model.DasTable
import com.intellij.database.model.RawDataSource
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shire.database.DatabaseSchemaAssistant
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider

enum class DatabaseFunction(val funName: String) {
    Table("table"),
    Column("column");

    companion object {
        fun fromString(value: String): DatabaseFunction? {
            return values().firstOrNull { it.funName == value }
        }
    }
}

class DatabaseFunctionProvider : ToolchainFunctionProvider {
    override fun isApplicable(project: Project, funcName: String): Boolean {
        return DatabaseFunction.values().any { it.funName == funcName }
    }

    override fun execute(
        project: Project,
        funcName: String,
        args: List<Any>,
        allVariables: Map<String, Any?>,
    ): Any {
        val databaseFunction =
            DatabaseFunction.fromString(funcName) ?: throw IllegalArgumentException("Invalid Database function name")

        when (databaseFunction) {
            DatabaseFunction.Table -> {
                if (args.isEmpty()) {
                    val dataSource = DatabaseSchemaAssistant.getAllRawDatasource(project).firstOrNull()
                        ?: return "ShireError: No database found"
                    return DatabaseSchemaAssistant.getTableByDataSource(dataSource)
                }

                val dbName = args.first()
                // [accounts, payment_limits, transactions]
                var result = mutableListOf<DasTable>()
                when (dbName) {
                    is String -> {
                        if (dbName.startsWith("[") && dbName.endsWith("]")) {
                            val tableNames = dbName.substring(1, dbName.length - 1).split(",")
                            result = tableNames.map {
                                getTable(project, it.trim())
                            }.flatten().toMutableList()
                        }
                    }
                    is List<*> -> {
                        result = dbName.map {
                            getTable(project, it as String)
                        }.flatten().toMutableList()
                    }

                    else -> {

                    }
                }

                return result
            }

            DatabaseFunction.Column -> {
                if (args.isEmpty()) {
                    val allTables = DatabaseSchemaAssistant.getAllTables(project)
                    return allTables.map {
                        DatabaseSchemaAssistant.getTableColumn(it)
                    }
                }

                when (val first = args[0]) {
                    is RawDataSource -> {
                        return if (args.size == 1) {
                            DatabaseSchemaAssistant.getTableByDataSource(first)
                        } else {
                            DatabaseSchemaAssistant.getTable(first, args[1] as String)
                        }
                    }

                    is DasTable -> {
                        return DatabaseSchemaAssistant.getTableColumn(first)
                    }

                    is List<*> -> {
                        return when (first.first()) {
                            is RawDataSource -> {
                                return first.map {
                                    DatabaseSchemaAssistant.getTableByDataSource(it as RawDataSource)
                                }
                            }

                            is DasTable -> {
                                return first.map {
                                    DatabaseSchemaAssistant.getTableColumn(it as DasTable)
                                }
                            }

                            else -> {
                                "ShireError: Table function requires a data source or a list of table names"
                            }
                        }
                    }

                    else -> {
                        logger<DatabaseFunctionProvider>().error("args types: ${first.javaClass}")
                        return "ShireError: Table function requires a data source or a list of table names"
                    }
                }
            }
        }
    }

    private fun getTable(project: Project, dbName: String): List<DasTable> {
        val database = DatabaseSchemaAssistant.getDatabase(project, dbName)
            ?: return emptyList()
        return DatabaseSchemaAssistant.getTableByDataSource(database)
    }
}
