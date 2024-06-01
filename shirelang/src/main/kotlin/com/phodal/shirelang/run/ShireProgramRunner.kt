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
import com.intellij.openapi.components.service
import com.intellij.openapi.components.serviceOrNull
import com.intellij.openapi.fileEditor.FileDocumentManager
import java.util.concurrent.atomic.AtomicReference

class ShireProgramRunner : GenericProgramRunner<RunnerSettings>(), Disposable {
    private val RUNNER_ID: String = "DevInsProgramRunner"

    private val connection = ApplicationManager.getApplication().messageBus.connect(this)

    private var isSubscribed = false

    override fun getRunnerId(): String = RUNNER_ID

    override fun canRun(executorId: String, profile: RunProfile) = profile is ShireConfiguration

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        if (environment.runProfile !is ShireConfiguration) return null
        val devInState = state as ShireRunConfigurationProfileState

        FileDocumentManager.getInstance().saveAllDocuments()

        val result = AtomicReference<RunContentDescriptor>()

        //避免多次subscribe
        if(!isSubscribed) {
            connection.subscribe(ShireRunListener.TOPIC, object : ShireRunListener {
                override fun runFinish(string: String, event: ProcessEvent, scriptPath: String) {
                    environment.project.getService(ShireProcessProcessor::class.java).process(string, event, scriptPath)
                }
            })

            isSubscribed = true
        }

        ApplicationManager.getApplication().invokeAndWait {
            val showRunContent = showRunContent(devInState.execute(environment.executor, this), environment)
            result.set(showRunContent)
        }

        return result.get()
    }

    override fun dispose() {}
}
