package com.phodal.shirelang.run

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.console.ConsoleViewWrapperBase
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.ui.components.panels.NonOpaquePanel
import com.phodal.shirecore.agent.CustomAgent
import com.phodal.shirecore.agent.CustomAgentExecutor
import com.phodal.shirecore.provider.LlmProvider
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.compiler.error.SHIRE_ERROR
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.flow.ShireConversationService
import com.phodal.shirelang.utils.ShireCoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.BorderLayout
import javax.swing.JComponent

open class ShireRunConfigurationProfileState(
    private val myProject: Project,
    private val configuration: ShireConfiguration,
) : RunProfileState {
    private val llm: LlmProvider = LlmProvider.create(myProject)

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = ShireProcessHandler(configuration.name)
        ProcessTerminatedListener.attach(processHandler)

        val sb = StringBuilder()

        processHandler.addProcessListener(object : ProcessAdapter() {
            var result = ""
            override fun processTerminated(event: ProcessEvent) {
                super.processTerminated(event)

                ApplicationManager.getApplication().messageBus
                    .syncPublisher(ShireRunListener.TOPIC)
                    .runFinish(result, event, configuration.getScriptPath())
            }

            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                super.onTextAvailable(event, outputType)
                result = sb.toString()
            }
        })

        val executionConsole = ConsoleViewImpl(myProject, true)
        val console = object : ConsoleViewWrapperBase(executionConsole) {
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
        }

        // start message log in here
        console.addMessageFilter { line, _ ->
            sb.append(line)
            null
        }

        console.attachToProcess(processHandler)

        val file: ShireFile? = ShireFile.lookup(myProject, configuration.getScriptPath())
        if (file == null) {
            console.print("File not found: ${configuration.getScriptPath()}", ConsoleViewContentType.ERROR_OUTPUT)
            processHandler.destroyProcess()
            return DefaultExecutionResult(console, processHandler)
        }

        // save the run result
        val compiler = ShireCompiler(myProject, file)
        val compileResult = compiler.compile()

        myProject.service<ShireConversationService>().createConversation(configuration.getScriptPath(), compileResult)

        val output = compileResult.output
        val agent = compileResult.executeAgent

        output.split("\n").forEach {
            if (it.contains(SHIRE_ERROR)) {
                console.print(it, ConsoleViewContentType.LOG_ERROR_OUTPUT)
            } else {
                console.print(it, ConsoleViewContentType.USER_INPUT)
            }
            console.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
        }

        console.print("\n--------------------\n", ConsoleViewContentType.NORMAL_OUTPUT)

        if (output.contains(SHIRE_ERROR)) {
            processHandler.exitWithError()
            return DefaultExecutionResult(console, processHandler)
        }

        if (agent != null) {
            agentRun(output, console, processHandler, agent)
        } else {
            defaultRun(output, console, processHandler, compileResult.isLocalCommand)
        }

        return DefaultExecutionResult(console, processHandler)
    }

    private fun agentRun(
        output: String,
        console: ConsoleViewWrapperBase,
        processHandler: ProcessHandler,
        agent: CustomAgent
    ) {
        ApplicationManager.getApplication().invokeLater {
            val stringFlow: Flow<String>? = CustomAgentExecutor(project = myProject).execute(output, agent)
            if (stringFlow != null) {
                ShireCoroutineScope.scope(myProject).launch {
                    val llmResult = StringBuilder()
                    runBlocking {
                        stringFlow.collect {
                            llmResult.append(it)
                            console.print(it, ConsoleViewContentType.NORMAL_OUTPUT)
                        }
                    }

                    console.print("\nDone!", ConsoleViewContentType.SYSTEM_OUTPUT)
                    myProject.service<ShireConversationService>()
                        .updateLlmResponse(configuration.getScriptPath(), llmResult.toString())
                    processHandler.detachProcess()
                }
            }
        }
    }

    private fun defaultRun(
        output: String,
        console: ConsoleViewWrapperBase,
        processHandler: ProcessHandler,
        isLocalMode: Boolean
    ) {
        ApplicationManager.getApplication().invokeLater {
            if (isLocalMode) {
                console.print("Local command detected, running in local mode", ConsoleViewContentType.SYSTEM_OUTPUT)
                processHandler.detachProcess()
                return@invokeLater
            }

            ShireCoroutineScope.scope(myProject).launch {
                val llmResult = StringBuilder()
                runBlocking {
                    llm.stream(output, "", false).collect {
                        llmResult.append(it)
                        console.print(it, ConsoleViewContentType.NORMAL_OUTPUT)
                    }
                }

                console.print("\nDone!", ConsoleViewContentType.SYSTEM_OUTPUT)
                myProject.service<ShireConversationService>()
                    .updateLlmResponse(configuration.getScriptPath(), llmResult.toString())
                processHandler.detachProcess()
            }
        }
    }
}

