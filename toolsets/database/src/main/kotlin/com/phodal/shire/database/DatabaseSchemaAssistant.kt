package com.phodal.shire.database

import com.intellij.database.console.DatabaseRunners
import com.intellij.database.console.JdbcConsole
import com.intellij.database.console.JdbcConsoleProvider
import com.intellij.database.console.evaluation.EvaluationRequest
import com.intellij.database.console.session.DatabaseSessionManager
import com.intellij.database.dataSource.InterruptibleDatabaseConnection
import com.intellij.database.dataSource.LocalDataSource
import com.intellij.database.dataSource.connection.DGDepartment
import com.intellij.database.dataSource.connection.statements.Configuration
import com.intellij.database.dataSource.connection.statements.ReusableSmartStatement
import com.intellij.database.dataSource.connection.statements.SmartStatements
import com.intellij.database.datagrid.*
import com.intellij.database.editor.DatabaseEditorHelper
import com.intellij.database.editor.DatabaseEditorHelperCore
import com.intellij.database.intentions.RunQueryInConsoleIntentionAction.Manager.chooseAndRunRunners
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.model.RawDataSource
import com.intellij.database.psi.DbDataSource
import com.intellij.database.psi.DbPsiFacade
import com.intellij.database.script.PersistenceConsoleProvider
import com.intellij.database.settings.DatabaseSettings
import com.intellij.database.util.DasUtil
import com.intellij.database.util.ErrorHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.sql.psi.SqlPsiFacade
import com.intellij.testFramework.LightVirtualFile
import java.util.concurrent.CompletableFuture

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

    fun executeSqlQuery(project: Project, sql: String): String {
        val file = LightVirtualFile("temp.sql", sql)
        val psiFile = PsiManager.getInstance(project).findFile(file)!!

        val dataSource = getAllRawDatasource(project).firstOrNull()
            ?: throw IllegalArgumentException("ShireError[Database]: No database found")

        val execOptions = DatabaseSettings.getSettings().execOptions.last()
        val console: JdbcConsole? = JdbcConsole.getActiveConsoles(project).firstOrNull()
            ?: JdbcConsoleProvider.getValidConsole(project, file)
            ?: createConsole(project, file)

        if (console != null) {
            return ApplicationManager.getApplication().executeOnPooledThread<String> {
                val future: CompletableFuture<String> = CompletableFuture()
                val messageBus = console.session.messageBus
                val newConsoleRequest = EvaluationRequest.newRequest(console, sql, dataSource.dbms)
                messageBus.dataProducer.processRequest(newConsoleRequest)
                messageBus.addConsumer(object : DataConsumer {
                    var result = mutableListOf<GridRow>()
                    override fun setColumns(context: GridDataRequest.Context, columns: Array<out GridColumn>) {
                        super.setColumns(context, columns)
                    }

                    override fun addRows(context: GridDataRequest.Context, rows: MutableList<out GridRow>) {
                        super.addRows(context, rows)
                        result += rows;
                    }

                    override fun afterLastRowAdded(context: GridDataRequest.Context, total: Int) {
                        super.afterLastRowAdded(context, total)
                        future.complete(result.toString())
                    }
                })

                return@executeOnPooledThread future.get()
            }.get()
        }

        val editor = FileEditorManager.getInstance(project).selectedTextEditor
            ?: throw IllegalArgumentException("ShireError[Database]: No editor found")

        val scriptModel = console?.scriptModel
            ?: SqlPsiFacade.getInstance(project).createScriptModel(psiFile)

        val dasNamespace = dataSource.model.currentRootNamespace
        DatabaseEditorHelper.openConsoleForFile(project, dataSource, dasNamespace, file)

        val info = JdbcConsoleProvider.Info(psiFile, psiFile, editor as EditorEx, scriptModel, execOptions, null)
        val runners: MutableList<PersistenceConsoleProvider.Runner> = DatabaseRunners.getAttachDataSourceRunners(info)
        if (runners.size == 1) {
            val runner = runners.first()
            val handler = ErrorHandler()
            val searchPath = DatabaseEditorHelperCore.getSearchPath(psiFile)
            DatabaseSessionManager
                .getFacade(
                    project,
                    dataSource as LocalDataSource,
                    null,
                    searchPath,
                    false,
                    handler,
                    DGDepartment.DATA_IMPORT
                ).runSync { connection: InterruptibleDatabaseConnection ->
                    var statement: ReusableSmartStatement<String>? = null
                    try {
                        statement = SmartStatements.poweredBy(connection).simple(Configuration.default).reuse()
                        runner.run()
                    } finally {
                        statement?.close()
                    }
                }

        } else {
            chooseAndRunRunners(runners, info.editor, null)
        }

        return "Error"
    }

    private fun createConsole(project: Project, file: LightVirtualFile): JdbcConsole? {
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