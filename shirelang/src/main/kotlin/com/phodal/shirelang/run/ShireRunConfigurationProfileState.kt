package com.phodal.shirelang.run

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.console.ConsoleViewWrapperBase
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.ui.components.panels.NonOpaquePanel
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.provider.context.ActionLocationEditor
import com.phodal.shirelang.compiler.ShireSyntaxAnalyzer
import com.phodal.shirelang.psi.ShireFile
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
    private var executionConsole: ConsoleViewImpl? = null
    var console: ShireConsoleView? = null

    var isShowRunContent = true

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult {
        executionConsole = ConsoleViewImpl(myProject, true)
        console = ShireConsoleView(executionConsole!!)

        val processHandler = ShireProcessHandler(configuration.name)
        ProcessTerminatedListener.attach(processHandler)

        val sb = StringBuilder()
        val processAdapter = ShireProcessAdapter(sb, configuration)
        processHandler.addProcessListener(processAdapter)

        // start message log in here
        console!!.addMessageFilter { line, _ ->
            sb.append(line)
            null
        }

        console!!.attachToProcess(processHandler)

        val shireFile: ShireFile? = ShireFile.lookup(myProject, configuration.getScriptPath())
        if (shireFile == null) {
            console!!.print("File not found: ${configuration.getScriptPath()}", ConsoleViewContentType.ERROR_OUTPUT)
            processHandler.destroyProcess()
            return DefaultExecutionResult(console, processHandler)
        }

        val shireRunner = ShireRunner(
            shireFile, myProject, console!!, configuration, configuration.getVariables(), processHandler
        )

        val syntaxAnalyzer = ShireSyntaxAnalyzer(myProject, shireFile, ActionLocationEditor.defaultEditor(myProject))
        val parsedResult = syntaxAnalyzer.parse()

        val location = parsedResult.config?.actionLocation
        if (location == ShireActionLocation.TERMINAL_MENU || location == ShireActionLocation.COMMIT_MENU) {
            isShowRunContent = false
        }

        console!!.print("Prepare for running ${configuration.name}...\n", ConsoleViewContentType.NORMAL_OUTPUT)
        ShireCoroutineScope.scope(myProject).launch {
            try {
                val llmOutput = shireRunner.execute(parsedResult)
                processAdapter.setLlmOutput(llmOutput)
            } catch (e: Exception) {
                console!!.print("Failed to run ${configuration.name}: ${e.message}\n", ConsoleViewContentType.ERROR_OUTPUT)
            }
        }

        return DefaultExecutionResult(console, processHandler)
    }

    override fun dispose() {
        console?.dispose()
        executionConsole?.dispose()
    }

}

class ShireConsoleView(private val executionConsole: ConsoleViewImpl) :
    ConsoleViewWrapperBase(executionConsole) {
    override fun getComponent(): JComponent = myPanel
    private var myPanel: NonOpaquePanel = NonOpaquePanel(BorderLayout())

    init {
        val baseComponent = delegate.component
        myPanel.add(baseComponent, BorderLayout.CENTER)

        val actionGroup = DefaultActionGroup(*executionConsole.createConsoleActions())
        val toolbar = ActionManager.getInstance().createActionToolbar("BuildConsole", actionGroup, false)
        toolbar.targetComponent = baseComponent
        myPanel.add(toolbar.component, BorderLayout.EAST)
    }

    override fun dispose() {
        super.dispose()
        executionConsole.dispose()
    }
}

class ShireProcessAdapter(private val sb: StringBuilder, val configuration: ShireConfiguration) : ProcessAdapter() {
    var result = ""
    private var llmOutput: String = ""

    override fun processTerminated(event: ProcessEvent) {
        super.processTerminated(event)

        ApplicationManager.getApplication().messageBus
            .syncPublisher(ShireRunListener.TOPIC)
            .runFinish(result, llmOutput, event, configuration.getScriptPath())
    }

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        super.onTextAvailable(event, outputType)
        result = sb.toString()
    }

    fun setLlmOutput(llmOutput: String?) {
        if (llmOutput != null) {
            this.llmOutput = llmOutput
        }
    }

    fun getLlmOutput(): String? {
        return llmOutput
    }
}

