package com.phodal.shirelang.run.runner

import com.intellij.execution.console.ConsoleViewWrapperBase
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.phodal.shire.llm.LlmProvider
import com.phodal.shirelang.run.ShireConfiguration
import com.phodal.shirelang.run.flow.ShireConversationService
import com.phodal.shirelang.utils.ShireCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ShireDefaultRunner(
    override val myProject: Project,
    override val configuration: ShireConfiguration,
    override val console: ConsoleViewWrapperBase,
    override val processHandler: ProcessHandler,
    override val prompt: String,
    private val isLocalMode: Boolean,
 ) : ShireRunner(configuration, processHandler, console, myProject, prompt) {
   override fun execute() {
        ApplicationManager.getApplication().invokeLater {
            if (isLocalMode) {
                console.print("Local command detected, running in local mode", ConsoleViewContentType.SYSTEM_OUTPUT)
                processHandler.detachProcess()
                return@invokeLater
            }

            ShireCoroutineScope.scope(myProject).launch {
                val llmResult = StringBuilder()
                runBlocking {
                    LlmProvider.provider(myProject)?.stream(prompt, "", false)?.collect {
                        llmResult.append(it)
                        console.print(it, ConsoleViewContentType.NORMAL_OUTPUT)
                    } ?: console.print("No LLM provider found", ConsoleViewContentType.ERROR_OUTPUT)
                }

                console.print("\nDone!", ConsoleViewContentType.SYSTEM_OUTPUT)
                myProject.getService(ShireConversationService::class.java)
                    .updateLlmResponse(configuration.getScriptPath(), llmResult.toString())
                processHandler.detachProcess()
            }
        }
    }
}

