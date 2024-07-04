package com.phodal.shirelang.run

import com.phodal.shirelang.run.flow.ShireProcessProcessor
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.runners.showRunContent
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import java.util.concurrent.atomic.AtomicReference

class ShireProgramRunner : GenericProgramRunner<RunnerSettings>(), Disposable {
    private val RUNNER_ID: String = "ShireProgramRunner"

    private val connection = ApplicationManager.getApplication().messageBus.connect(this)

    private var isSubscribed = false

    override fun getRunnerId(): String = RUNNER_ID

    override fun canRun(executorId: String, profile: RunProfile) = profile is ShireConfiguration

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        if (environment.runProfile !is ShireConfiguration) return null
        val shireState = state as ShireRunConfigurationProfileState

//        FileDocumentManager.getInstance().saveAllDocuments()

        val result = AtomicReference<RunContentDescriptor>()

        if (!isSubscribed) {
            connection.subscribe(ShireRunListener.TOPIC, object : ShireRunListener {
                override fun runFinish(string: String, event: ProcessEvent, scriptPath: String) {
                    val consoleView = (environment.state as? ShireRunConfigurationProfileState)?.console
                    environment.project.getService(ShireProcessProcessor::class.java)
                        .process(string, event, scriptPath, consoleView)

                    ApplicationManager.getApplication().invokeAndWait {
                        val showRunContent = showRunContent(
                            shireState.execute(environment.executor, this@ShireProgramRunner),
                            environment
                        )
                        result.set(showRunContent)
                    }
                }
            })

            isSubscribed = true
        }

        return result.get()
    }

    override fun dispose() {
        connection.dispose()
    }
}
