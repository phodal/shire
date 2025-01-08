package com.phodal.shirelang.run

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.*
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.config.InteractionType
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.provider.streaming.OnStreamingService
import com.phodal.shirecore.runner.ShireProcessHandler
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.runner.ShireRunner
import kotlinx.coroutines.launch

/**
 * ShireRunConfigurationProfileState is a class that represents the state of a run configuration profile in the Shire plugin for Kotlin.
 * It implements the RunProfileState interface.
 *
 */
class ShireRunConfigurationProfileState(
    private val myProject: Project,
    private val configuration: ShireConfiguration,
) : RunProfileState, Disposable {
    private var executionConsole: ShireExecutionConsole =
        ShireExecutionConsole(myProject, true, configuration = configuration)
    var console: ShireConsoleView = ShireConsoleView(executionConsole)

    var isShowRunContent = true

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = ShireProcessHandler(configuration.name)
        ProcessTerminatedListener.attach(processHandler)

        val processAdapter = ShireProcessAdapter(configuration, console)
        processHandler.addProcessListener(processAdapter)

        console.attachToProcess(processHandler)

        val shireFile: ShireFile? = ShireFile.lookup(myProject, configuration.getScriptPath())
        if (shireFile == null) {
            console.print("File not found: ${configuration.getScriptPath()}", ConsoleViewContentType.ERROR_OUTPUT)
            processHandler.exitWithError()
            return DefaultExecutionResult(console, processHandler)
        }

        val shireRunner = ShireRunner(
            myProject, console, configuration, configuration.getVariables(), processHandler
        ).also {
            console.bindShireRunner(it)
            processHandler.addProcessListener(object : ProcessListener {
                override fun processTerminated(event: ProcessEvent) {
                    it.cancel()
                }
            })
        }

        val parsedResult = ShireRunner.preAnalysisAndLocalExecute(shireFile, myProject)

        val location = parsedResult.config?.actionLocation
        if (location == ShireActionLocation.TERMINAL_MENU || location == ShireActionLocation.COMMIT_MENU) {
            isShowRunContent = false
        }

        val interaction = parsedResult.config?.interaction
        if (interaction == InteractionType.RightPanel) {
            isShowRunContent = false
        }

        console.print("Prepare for running ${configuration.name}...\n", ConsoleViewContentType.NORMAL_OUTPUT)
        ShireCoroutineScope.scope(myProject).launch {
            try {
                val llmOutput = shireRunner.execute(parsedResult)
                processAdapter.setLlmOutput(llmOutput)

                myProject.getService(OnStreamingService::class.java)?.onDone(myProject)
            } catch (e: Exception) {
                console.print(
                    "Failed to run ${configuration.name}: ${e.message}\n",
                    ConsoleViewContentType.LOG_ERROR_OUTPUT
                )
                console.print(e.stackTraceToString(), ConsoleViewContentType.ERROR_OUTPUT)
            }
        }

        return DefaultExecutionResult(console, processHandler)
    }

    override fun dispose() {
        console.dispose()
        executionConsole.dispose()
    }
}

