package com.phodal.shirelang.run

import com.intellij.build.BuildView
import com.intellij.build.DefaultBuildDescriptor
import com.intellij.build.events.BuildEvent
import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.*
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.ide.DataManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskType
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfigurationViewManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiManager
import com.intellij.ui.components.panels.NonOpaquePanel
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.config.InteractionType
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.provider.streaming.OnStreamingService
import com.phodal.shirecore.runner.ShireProcessHandler
import com.phodal.shirecore.runner.console.ShireConsoleViewBase
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.precompile.preAnalysisSyntax
import com.phodal.shirelang.run.runner.ShireRunner
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import javax.swing.JComponent

/**
 * ShireRunConfigurationProfileState is a class that represents the state of a run configuration profile in the Shire plugin for Kotlin.
 * It implements the RunProfileState interface.
 *
 */
open class ShireRunConfigurationProfileState(
    private val myProject: Project,
    private val configuration: ShireConfiguration,
) : RunProfileState, Disposable {
    private var executionConsole: ShireExecutionConsole? = null
    var console: ShireConsoleView? = null

    var isShowRunContent = true

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult {
        executionConsole = ShireExecutionConsole(myProject, true, configuration = configuration)
        console = ShireConsoleView(executionConsole!!)

        val processHandler = ShireProcessHandler(configuration.name)
        ProcessTerminatedListener.attach(processHandler)

        val processAdapter = ShireProcessAdapter(configuration, console)
        processHandler.addProcessListener(processAdapter)

        console!!.attachToProcess(processHandler)

        var shireFile: ShireFile? = ShireFile.lookup(myProject, configuration.getScriptPath())
        if (shireFile == null) {
            shireFile = tryLoadFromDataContext()
        }

        if (shireFile == null) {
            console!!.print("File not found: ${configuration.getScriptPath()}", ConsoleViewContentType.ERROR_OUTPUT)
            processHandler.exitWithError()
            return DefaultExecutionResult(console, processHandler)
        }

        val shireRunner = ShireRunner(
            shireFile, myProject, console!!, configuration, configuration.getVariables(), processHandler
        ).also {
            console?.bindShireRunner(it)
            processHandler.addProcessListener(object : ProcessListener {
                override fun processTerminated(event: ProcessEvent) {
                    it.cancel()
                }
            })
        }

        val parsedResult = preAnalysisSyntax(shireFile, myProject)

        val location = parsedResult.config?.actionLocation
        if (location == ShireActionLocation.TERMINAL_MENU || location == ShireActionLocation.COMMIT_MENU) {
            isShowRunContent = false
        }

        val interaction = parsedResult.config?.interaction
        if (interaction == InteractionType.RightPanel) {
            isShowRunContent = false
        }

        console!!.print("Prepare for running ${configuration.name}...\n", ConsoleViewContentType.NORMAL_OUTPUT)
        ShireCoroutineScope.scope(myProject).launch {
            try {
                val llmOutput = shireRunner.execute(parsedResult)
                processAdapter.setLlmOutput(llmOutput)

                myProject.getService(OnStreamingService::class.java)?.onDone(myProject)
            } catch (e: Exception) {
                console!!.print(
                    "Failed to run ${configuration.name}: ${e.message}\n",
                    ConsoleViewContentType.LOG_ERROR_OUTPUT
                )
                console!!.print(e.stackTraceToString(), ConsoleViewContentType.ERROR_OUTPUT)
            }
        }

        return DefaultExecutionResult(console, processHandler)
    }

    private fun tryLoadFromDataContext(): ShireFile? {
        val dataContext = DataManager.getInstance().dataContextFromFocusAsync.blockingGet(10000)
            ?: throw IllegalStateException("No data context found")

        val data = SimpleDataContext.getProjectContext(myProject).getData(SHIRE_VIRTUAL_KEY)

        return dataContext.getData(SHIRE_VIRTUAL_KEY) ?: data
    }

    override fun dispose() {
        console?.dispose()
        executionConsole?.dispose()
    }

    companion object {
        val SHIRE_VIRTUAL_KEY: DataKey<ShireFile> = DataKey.create("shireVirtualKey")
    }
}

class ShireConsoleView(private val executionConsole: ShireExecutionConsole) :
    ShireConsoleViewBase(executionConsole) {

    override fun getComponent(): JComponent = myPanel

    private var myPanel: NonOpaquePanel = NonOpaquePanel(BorderLayout())

    private var shireRunner: ShireRunner? = null
    private val id = ProjectSystemId("Shire")
    private fun createTaskId() =
        ExternalSystemTaskId.create(id, ExternalSystemTaskType.RESOLVE_PROJECT, executionConsole.project)

    private val scriptPath = executionConsole.configuration.getScriptPath()

    val task = createTaskId()
    val buildDescriptor: DefaultBuildDescriptor =
        DefaultBuildDescriptor(task.id, "Shire", scriptPath, System.currentTimeMillis())

    val viewManager: ExternalSystemRunConfigurationViewManager =
        executionConsole.project.getService(ExternalSystemRunConfigurationViewManager::class.java)

    private val buildView: BuildView = object : BuildView(
        executionConsole.project,
        executionConsole,
        buildDescriptor,
        "build.toolwindow.run.selection.state",
        viewManager
    ) {
        override fun onEvent(buildId: Any, event: BuildEvent) {
            super.onEvent(buildId, event)
            viewManager.onEvent(buildId, event)
        }
    }

    init {
        val baseComponent = buildView.component
        myPanel.add(baseComponent, BorderLayout.EAST)

        executionConsole.getProcessHandler()?.let {
            buildView.attachToProcess(it)
        }

        myPanel.add(delegate.component, BorderLayout.CENTER)
    }

    fun output(clearAndStop: Boolean = true) = executionConsole.getOutput(clearAndStop)

    override fun cancelCallback(callback: (String) -> Unit) {
        shireRunner?.addCancelListener(callback)
    }

    fun getEditor(): Editor? {
        return executionConsole.editor
    }

    override fun isCanceled(): Boolean = shireRunner?.isCanceled() ?: super.isCanceled()

    fun bindShireRunner(runner: ShireRunner) {
        shireRunner = runner
    }

    override fun dispose() {
        super.dispose()
        executionConsole.dispose()
    }
}

class ShireProcessAdapter(val configuration: ShireConfiguration, val consoleView: ShireConsoleView?) :
    ProcessAdapter() {
    var result = ""
    private var llmOutput: String = ""

    override fun processTerminated(event: ProcessEvent) {
        super.processTerminated(event)

        ApplicationManager.getApplication().messageBus
            .syncPublisher(ShireRunListener.TOPIC)
            .runFinish(result, llmOutput, event, configuration.getScriptPath(), consoleView)
    }

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        super.onTextAvailable(event, outputType)
        result = consoleView?.output().toString()
    }

    fun setLlmOutput(llmOutput: String?) {
        if (llmOutput != null) {
            this.llmOutput = llmOutput
        }
    }
}

class ShireExecutionConsole(
    project: Project,
    viewer: Boolean,
    private var isStopped: Boolean = false,
    val configuration: ShireConfiguration,
) : ConsoleViewImpl(project, viewer) {
    private val outputBuilder = StringBuilder()
    private var processHandler: ShireProcessHandler? = null

    fun getProcessHandler(): ShireProcessHandler? {
        return processHandler
    }

    override fun attachToProcess(processHandler: ProcessHandler) {
        super.attachToProcess(processHandler)
        this.processHandler = processHandler as ShireProcessHandler
    }

    override fun print(text: String, contentType: ConsoleViewContentType) {
        super.print(text, contentType)
        if (!isStopped) outputBuilder.append(text)
    }

    fun getOutput(clearAndStop: Boolean): String {
        val output = outputBuilder.toString()
        if (clearAndStop) {
            isStopped = true
            outputBuilder.clear()
        }

        return output
    }

}