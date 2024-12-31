package com.phodal.shire.database

import com.intellij.database.console.DatabaseRunners
import com.intellij.database.console.JdbcConsole
import com.intellij.database.console.JdbcConsoleBase
import com.intellij.database.console.JdbcConsoleProvider
import com.intellij.database.console.evaluation.EvaluationRequest
import com.intellij.database.console.session.DatabaseSession
import com.intellij.database.console.session.DatabaseSessionManager
import com.intellij.database.console.session.DatabaseSessionManagerListener
import com.intellij.database.dataSource.DatabaseConnectionCore
import com.intellij.database.dataSource.InterruptibleDatabaseConnection
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
import com.intellij.database.util.DbImplUtil
import com.intellij.database.util.DbImplUtilCore
import com.intellij.database.util.ErrorHandler
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.sql.psi.SqlPsiFacade
import com.intellij.testFramework.LightVirtualFile
import java.util.concurrent.CompletableFuture

object DatabaseSchemaAssistant {
    fun getDataSources(project: Project): List<DbDataSource> = DbPsiFacade.getInstance(project).dataSources.toList()

    fun allRawDatasource(project: Project): List<RawDataSource> {
        val dbPsiFacade = DbPsiFacade.getInstance(project)
        return dbPsiFacade.dataSources.map { dataSource ->
            dbPsiFacade.getDataSourceManager(dataSource).dataSources
        }.flatten()
    }

    fun getDatabase(project: Project, dbName: String): RawDataSource? {
        return allRawDatasource(project).firstOrNull { it.name == dbName }
    }

    fun getAllTables(project: Project): List<DasTable> {
        return allRawDatasource(project).map {
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

        val dataSource = allRawDatasource(project).firstOrNull()
            ?: throw IllegalArgumentException("ShireError[Database]: No database found")

        val execOptions = DatabaseSettings.getSettings().execOptions.last()
        val console: JdbcConsole? = JdbcConsole.getActiveConsoles(project).firstOrNull()
            ?: JdbcConsoleProvider.getValidConsole(project, file)
            ?: createConsole(project, file)

        if (console != null) {
            val future: CompletableFuture<String> = CompletableFuture()
            return ApplicationManager.getApplication().executeOnPooledThread<String> {
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
                        result += rows
                        /// TODO: fix this use result.size instead of rows.size
                        if (rows.size < 100) {
                            future.complete(result.toString())
                        }
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
            var result = ""
            ApplicationManager.getApplication().invokeAndWait {
                result = runSingleRunner(psiFile, project, dataSource, runner, sql) ?: "Error"
            }
            return result
        } else {
            chooseAndRunRunners(runners, info.editor, null)
        }

        return "Error"
    }

    private fun runSingleRunner(
        psiFile: PsiFile,
        project: Project,
        dataSource: RawDataSource,
        runner: PersistenceConsoleProvider.Runner,
        query: String,
    ): String? {
        val future: CompletableFuture<String> = CompletableFuture()
        val localDs = DbImplUtilCore.getLocalDataSource(dataSource)

        val session = DatabaseSessionManager.getSession(project, localDs)
        val messageBus = session.messageBus
        messageBus.addConsumer(object : DataConsumer {
            var result = mutableListOf<GridRow>()
            override fun setColumns(context: GridDataRequest.Context, columns: Array<out GridColumn>) {
                super.setColumns(context, columns)
            }

            override fun addRows(context: GridDataRequest.Context, rows: MutableList<out GridRow>) {
                super.addRows(context, rows)
                result += rows
                println("rows")
                if (rows.size < 100) {
                    future.complete(result.toString())
                }
            }
        })

        val request =
            object : DataRequest.QueryRequest(session, query, DataRequest.newConstraints(dataSource.dbms), null) {}
        messageBus.dataProducer.processRequest(request)
        return future.get()
    }

    private fun runSingleRunner2(
        psiFile: PsiFile,
        project: Project,
        dataSource: RawDataSource,
        runner: PersistenceConsoleProvider.Runner,
    ) {
        val handler = ErrorHandler()
        //            val future: CompletableFuture<String> = CompletableFuture()
        //            return ApplicationManager.getApplication().executeOnPooledThread<String> {
        val localDs = DbImplUtilCore.getLocalDataSource(dataSource)
//        val namespace = localDs.model.currentRootNamespace
//        val searchPath = DatabaseEditorHelperCore.getSearchPath(psiFile)
        val searchPath = DatabaseEditorHelperCore.getSearchPath(psiFile)

        val facade = DatabaseSessionManager
            .getFacade(
                project, localDs, null, searchPath, false, handler, DGDepartment.DATA_EXPORT
            )

        val msgConnection = project.messageBus.connect()
        msgConnection.subscribe(DatabaseSessionManager.topic, object : DatabaseSessionManagerListener {
            override fun sessionOpened(session: DatabaseSession) {
                ///
            }

            override fun sessionClosed(session: DatabaseSession) {
                println("Session closed")
            }
        })

        facade.runSync { connection: InterruptibleDatabaseConnection ->
            var statement: ReusableSmartStatement<String>? = null
            try {
                statement = SmartStatements.poweredBy(connection).simple(Configuration.default).reuse()
                runner.run()
            } finally {
                //                            future.complete("Done")
                statement?.close()
            }
        }

        //                return@executeOnPooledThread future.get()
        //            }.get()
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

        return "TableName: ${table.name}, Columns: { $columns }"
    }
}