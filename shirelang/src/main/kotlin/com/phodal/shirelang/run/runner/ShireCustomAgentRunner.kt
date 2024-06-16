package com.phodal.shirelang.run.runner

import com.intellij.execution.console.ConsoleViewWrapperBase
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.phodal.shirecore.agent.CustomAgent
import com.phodal.shirecore.agent.CustomAgentExecutor
import com.phodal.shirelang.compiler.SymbolTable
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.ShireConfiguration
import com.phodal.shirelang.run.flow.ShireConversationService
import com.phodal.shirelang.utils.ShireCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ShireCustomAgentRunner(
    override val myProject: Project,
    override val configuration: ShireConfiguration,
    override val console: ConsoleViewWrapperBase,
    override val processHandler: ProcessHandler,
    override val input: String,
    override val symbolTable: SymbolTable,
    override val hole: HobbitHole?,
    private val agent: CustomAgent,
) : ShireRunner(configuration, processHandler, console, myProject, symbolTable, hole, input) {
    override fun execute() {
        ApplicationManager.getApplication().invokeLater {
            val prompt = this.compileShireTemplate()
            val stringFlow: Flow<String>? = CustomAgentExecutor(project = myProject).execute(prompt, agent)

            if (stringFlow == null) {
                console.print("No LLM provider found", ConsoleViewContentType.ERROR_OUTPUT)
                processHandler.detachProcess()
                return@invokeLater
            }

            ShireCoroutineScope.scope(myProject).launch {
                val llmResult = StringBuilder()
                runBlocking {
                    stringFlow.collect {
                        llmResult.append(it)
                        console.print(it, ConsoleViewContentType.NORMAL_OUTPUT)
                    }
                }

                console.print("\nDone!", ConsoleViewContentType.SYSTEM_OUTPUT)
                myProject.getService(ShireConversationService::class.java)
                    .updateLlmResponse(configuration.getScriptPath(), llmResult.toString())
                processHandler.detachProcess()
            }
        }
    }
}