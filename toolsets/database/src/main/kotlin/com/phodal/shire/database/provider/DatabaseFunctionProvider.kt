package com.phodal.shire.database.provider

import com.intellij.database.model.DasTable
import com.intellij.database.model.RawDataSource
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shire.database.DatabaseSchemaAssistant
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider

enum class DataFunction(val funName: String) {
    Database("database"),
    Table("table"),
    Column("column");

    companion object {
        fun fromString(value: String): DataFunction {
            return values().first { it.funName == value }
        }
    }
}

class DatabaseFunctionProvider : ToolchainFunctionProvider {
    override fun isApplicable(project: Project, funcName: String): Boolean {
        return DataFunction.values().any { it.funName == funcName }
    }

    override fun execute(
        project: Project,
        funcName: String,
        args: List<Any>,
        allVariables: Map<String, Any?>,
    ): Any {
        val dataFunction = DataFunction.fromString(funcName)
        return when (dataFunction) {
            DataFunction.Database -> {
                if (args.isEmpty()) {
                    logger<DatabaseFunctionProvider>().error("Database function requires a database name")
                    return ""
                }

                return DatabaseSchemaAssistant.getDatabase(project, args[0] as String)
            }
            DataFunction.Table -> {
                if (args.size < 2 ) {
                    logger<DatabaseFunctionProvider>().error("Table function requires a table name")
                    return ""
                }

                val dataSource = args[0] as? RawDataSource
                if (dataSource == null) {
                    logger<DatabaseFunctionProvider>().error("Table function requires a data source")
                    return ""
                }

                return DatabaseSchemaAssistant.getTable(args[0] as RawDataSource, args[1] as String)
            }
            DataFunction.Column -> {
                if (args.size < 2 ) {
                    logger<DatabaseFunctionProvider>().error("Column function requires a table name")
                    return ""
                }

                val table = args[0] as? DasTable
                if (table == null) {
                    logger<DatabaseFunctionProvider>().error("Column function requires a table")
                    return ""
                }

                return DatabaseSchemaAssistant.getTableColumn(table)
            }
        }
    }
}
