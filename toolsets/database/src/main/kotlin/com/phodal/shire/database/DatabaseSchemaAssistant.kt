package com.phodal.shire.database

import com.intellij.database.DataBus
import com.intellij.database.DatabaseTopics
import com.intellij.database.console.DatabaseRunners
import com.intellij.database.console.JdbcConsole
import com.intellij.database.console.JdbcConsoleProvider
import com.intellij.database.datagrid.DataConsumer
import com.intellij.database.datagrid.GridColumn
import com.intellij.database.datagrid.GridDataRequest
import com.intellij.database.datagrid.GridRow
import com.intellij.database.editor.DatabaseEditorHelper
import com.intellij.database.intentions.RunQueryInConsoleIntentionAction.Manager.chooseAndRunRunners
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.model.RawDataSource
import com.intellij.database.psi.DbDataSource
import com.intellij.database.psi.DbPsiFacade
import com.intellij.database.run.ConsoleDataRequest
import com.intellij.database.settings.DatabaseSettings
import com.intellij.database.util.DasUtil
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.sql.psi.SqlPsiFacade
import com.intellij.testFramework.LightVirtualFile

object DatabaseSchemaAssistant {
    fun getDataSources(project: Project): List<DbDataSource> {
        return DbPsiFacade.getInstance(project).dataSources.toList()
    }

    fun getAllRawDatasource(project: Project): List<RawDataSource> {
        val dbPsiFacade = DbPsiFacade.getInstance(project)
        return dbPsiFacade.dataSources.map { dataSource ->
            dbPsiFacade.getDataSourceManager(dataSource).dataSources
        }.flatten()
    }

    fun getDatabase(project: Project, dbName: String): RawDataSource? {
        return getAllRawDatasource(project).firstOrNull { it.name == dbName }
    }

    fun getAllTables(project: Project): List<DasTable> {
        val rawDataSources = getAllRawDatasource(project)
        return rawDataSources.map {
            val schemaName = it.name.substringBeforeLast('@')
            DasUtil.getTables(it).filter { table ->
                table.kind == ObjectKind.TABLE && (table.dasParent?.name == schemaName || isSQLiteTable(it, table))
            }
        }.flatten()
    }

    fun getTableByDataSource(dataSource: RawDataSource): List<DasTable> {
        return DasUtil.getTables(dataSource).toList()
    }

    fun getTable(dataSource: RawDataSource, tableName: String): List<DasTable> {
        val dasTables = DasUtil.getTables(dataSource)
        return dasTables.filter { it.name == tableName }.toList()
    }

    fun executeSqlQuery(project: Project, sql: String): List<Map<String, Any?>> {
        val file = LightVirtualFile("temp.sql", sql)
        val psiFile = PsiManager.getInstance(project).findFile(file)
            ?: throw IllegalArgumentException("ShireError[Database]: No file found")

//        val fileEditor = FileEditorManager.getInstance(project).openFile(file).firstOrNull()
//            ?: throw IllegalArgumentException("ShireError[Database]: No editor found")

        val editor = FileEditorManager.getInstance(project).selectedTextEditor
            ?: throw IllegalArgumentException("ShireError[Database]: No editor found")

        val dataSource = getAllRawDatasource(project).firstOrNull()
            ?: throw IllegalArgumentException("ShireError[Database]: No database found")

        val execOptions = DatabaseSettings.getSettings().execOptions.last()
        val activeConsoles = JdbcConsole.getActiveConsoles(project)
        val console: JdbcConsole? = activeConsoles.firstOrNull()
            ?: JdbcConsoleProvider.getValidConsole(project, file)
            ?: createConsole(project, file)

        val scriptModel = console?.scriptModel
            ?: SqlPsiFacade.getInstance(project).createScriptModel(psiFile)

        val dasNamespace = dataSource.model.currentRootNamespace
        DatabaseEditorHelper.openConsoleForFile(project, dataSource, dasNamespace, file)

        if (console == null) {
            val info = JdbcConsoleProvider.Info(
                psiFile, psiFile, editor as EditorEx, scriptModel, execOptions, null
            )
            chooseAndRunRunners(DatabaseRunners.getAttachDataSourceRunners(info), info.editor, null)
            return emptyList()
        }

        val messageBus = console.session.messageBus
        val newConsoleRequest = ConsoleDataRequest.newConsoleRequest(
            console,
            editor,
            scriptModel,
            false
        )
        if (newConsoleRequest != null) {
            messageBus.dataProducer.processRequest(newConsoleRequest)
        } else {
            console.executeQueries(editor, scriptModel, execOptions)
        }

        messageBus.addConsumer(object : DataConsumer {
            override fun setColumns(context: GridDataRequest.Context, columns: Array<out GridColumn>) {
                super.setColumns(context, columns)
            }

            override fun addRows(context: GridDataRequest.Context, rows: MutableList<out GridRow>) {
                super.addRows(context, rows)
            }
        })

        return emptyList()
    }

    fun createConsole(project: Project, file: LightVirtualFile): JdbcConsole? {
        val attached = JdbcConsoleProvider.findOrCreateSession(project, file) ?: return null
        return JdbcConsoleProvider.attachConsole(project, attached, file)
    }

    private fun isSQLiteTable(
        rawDataSource: RawDataSource,
        table: DasTable,
    ) = (rawDataSource.databaseVersion.name == "SQLite" && table.dasParent?.name == "main")

    fun getTableColumns(project: Project, tables: List<String> = emptyList()): List<String> {
        val dasTables = getAllTables(project)

        if (tables.isEmpty()) {
            return dasTables.map(::displayTable)
        }

        return dasTables.mapNotNull { table ->
            if (tables.contains(table.name)) {
                displayTable(table)
            } else {
                null
            }
        }
    }

    fun getTableColumn(table: DasTable): String = displayTable(table)

    private fun displayTable(table: DasTable): String {
        val dasColumns = DasUtil.getColumns(table)
        val columns = dasColumns.map { column ->
            "${column.name}: ${column.dasType.toDataType()}"
        }.joinToString(", ")

        return "TableName: ${table.name} Columns: { $columns }"
    }
}