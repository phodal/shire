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
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.ui.components.panels.NonOpaquePanel
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.provider.context.ActionLocationEditor
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.compiler.ShireTemplateCompiler
import com.phodal.shirelang.compiler.SHIRE_ERROR
import com.phodal.shirelang.compiler.ShireCompiledResult
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.flow.ShireConversationService
import com.phodal.shirelang.run.runner.CustomRemoteAgentLlmRunner
import com.phodal.shirelang.run.runner.ShireDefaultLlmRunner
import com.phodal.shirelang.run.runner.ShireLlmRunner
import com.phodal.shirelang.run.runner.ShireLlmRunnerContext
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

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult {
        executionConsole = ConsoleViewImpl(myProject, true)
        console = ShireConsoleView(executionConsole!!)

        val processHandler = ShireProcessHandler(configuration.name)
        ProcessTerminatedListener.attach(processHandler)

        val sb = StringBuilder()
        processHandler.addProcessListener(ShireProcessAdapter(sb, configuration))

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

        val runData = compileAndExecute(shireFile, myProject, console, configuration, configuration.getUserInput())

        if (runData.finalPrompt.isEmpty()) {
            console!!.print("No content to run", ConsoleViewContentType.ERROR_OUTPUT)
            processHandler.destroyProcess()
            return DefaultExecutionResult(console, processHandler)
        }

        if (runData.finalPrompt.contains(SHIRE_ERROR)) {
            processHandler.exitWithError()
            return DefaultExecutionResult(console, processHandler)
        }

        myProject.getService(ShireConversationService::class.java)
            .createConversation(configuration.getScriptPath(), runData.compileResult)

        normalExecute(runData, processHandler, myProject, console, configuration)

        return DefaultExecutionResult(console, processHandler)
    }

    override fun dispose() {
        console?.dispose()
        executionConsole?.dispose()
    }

    companion object {
        data class ShireRunListenerData(
            val hole: HobbitHole?,
            val editor: Editor?,
            val compileResult: ShireCompiledResult,
            val finalPrompt: String = "",
        )

        fun printCompiledOutput(
            console: ConsoleViewWrapperBase,
            promptText: String,
            shireConfiguration: ShireConfiguration,
        ) {
            console.print("Shire Script: ${shireConfiguration.getScriptPath()}\n", ConsoleViewContentType.SYSTEM_OUTPUT)
            console.print("Shire Script Compile output:\n", ConsoleViewContentType.SYSTEM_OUTPUT)
            promptText.split("\n").forEach {
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
        }

        fun compileAndExecute(
            shireFile: ShireFile,
            project: Project,
            consoleView: ShireConsoleView?,
            shireConfiguration: ShireConfiguration,
            userInput: String,
        ): ShireRunListenerData {
            val compiler = ShireCompiler(project, shireFile, ActionLocationEditor.defaultEditor(project))
            val compileResult = compiler.compile()

            val variableTable = compileResult.variableTable

            val input = compileResult.shireOutput
            val hobbitHole = compileResult.config
            val locationEditor = ActionLocationEditor.provide(project, hobbitHole?.actionLocation)

            val templateCompiler = ShireTemplateCompiler(project, hobbitHole, variableTable, input)
            if (userInput.isNotEmpty()) {
                templateCompiler.putCustomVariable("input", userInput)
            }

            val promptText = templateCompiler.compile()
            printCompiledOutput(consoleView!!, promptText, shireConfiguration)

            // check prompt text had content or just new line with whitespace
            val promptTextTrim = promptText.trim()

            return ShireRunListenerData(hobbitHole, locationEditor, compileResult, promptTextTrim)
        }

    }

    fun normalExecute(
        runData: ShireRunListenerData,
        processHandler: ShireProcessHandler,
        project: Project,
        console: ShireConsoleView?,
        configuration: ShireConfiguration,
    ) {
        val agent = runData.compileResult.executeAgent
        val shireLlmRunnerContext = ShireLlmRunnerContext(
            configuration = configuration,
            processHandler = processHandler,
            console = console!!,
            myProject = project,
            hole = runData.hole,
            prompt = runData.finalPrompt,
            editor = runData.editor,
        )
        val shireLlmRunner: ShireLlmRunner = when {
            agent != null -> {
                CustomRemoteAgentLlmRunner(shireLlmRunnerContext, agent)
            }

            else -> {
                val isLocalMode = runData.compileResult.isLocalCommand
                ShireDefaultLlmRunner(shireLlmRunnerContext, isLocalMode)
            }
        }

        shireLlmRunner.prepareTask()
        shireLlmRunner.execute { response, textRange ->
            var currentFile: PsiFile? = null
            runData.editor?.virtualFile?.also {
                currentFile = runReadAction { PsiManager.getInstance(project).findFile(it) }
            }

            val context = PostCodeHandleContext(
                selectedEntry = runData.hole?.pickupElement(project, runData.editor),
                currentLanguage = currentFile?.language,
                currentFile = currentFile,
                genText = response,
                modifiedTextRange = textRange,
                editor = runData.editor,
            )

            runData.hole?.executeStreamingEndProcessor(project, console, context)
            runData.hole?.executeAfterStreamingProcessor(project, console, context)
        }
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
}

