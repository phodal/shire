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
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.ui.components.panels.NonOpaquePanel
import com.phodal.shirelang.compile.VariableTemplateCompiler
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.compiler.SymbolTable
import com.phodal.shirelang.compiler.error.SHIRE_ERROR
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.flow.ShireConversationService
import com.phodal.shirelang.run.runner.ShireCustomAgentRunner
import com.phodal.shirelang.run.runner.ShireDefaultRunner
import com.phodal.shirelang.run.runner.ShireRunner
import com.phodal.shirelang.run.runner.SymbolResolver
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
) : RunProfileState {
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

        val editor = FileEditorManager.getInstance(myProject).selectedTextEditor
        val compiler = ShireCompiler(myProject, file, editor)
        val compileResult = compiler.compile()

        val symbolTable = compileResult.symbolTable

        // translate template for handle reset for the conversation

        myProject.getService(ShireConversationService::class.java)
            .createConversation(configuration.getScriptPath(), compileResult)

        val compiledOutput = compileShireTemplate(myProject, compileResult.config!!, symbolTable, compileResult.output)
        val agent = compileResult.executeAgent

        compiledOutput.split("\n").forEach {
            when {
                it.contains(SHIRE_ERROR) -> {
                    console.print(it, ConsoleViewContentType.LOG_ERROR_OUTPUT)
                }

                else -> {
                    console.print(it, ConsoleViewContentType.USER_INPUT)
                }
            }
            console.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
        }

        console.print("\n--------------------\n", ConsoleViewContentType.NORMAL_OUTPUT)

        if (compiledOutput.contains(SHIRE_ERROR)) {
            processHandler.exitWithError()
            return DefaultExecutionResult(console, processHandler)
        }

        val shireRunner: ShireRunner = if (agent != null) {
            ShireCustomAgentRunner(myProject, configuration, console, processHandler, compiledOutput, agent)
        } else {
            val isLocalMode = compileResult.isLocalCommand
            ShireDefaultRunner(myProject, configuration, console, processHandler, compiledOutput, isLocalMode)
        }

        shireRunner.execute()

        return DefaultExecutionResult(console, processHandler)
    }
}

fun compileShireTemplate(myProject: Project, hole: HobbitHole, symbolTable: SymbolTable, input: String): String {
    val currentEditor = VariableTemplateCompiler.defaultEditor(myProject)
    val currentElement = VariableTemplateCompiler.defaultElement(myProject, currentEditor)

    if (currentElement != null && currentEditor != null) {
        val additionalMap: Map<String, String> = SymbolResolver(myProject, currentEditor, hole).resolve(symbolTable)

        val file = currentElement.containingFile
        val templateCompiler = VariableTemplateCompiler(file.language, file)

        templateCompiler.set(additionalMap)
        return templateCompiler.compile(input)
    }

    return input
}

