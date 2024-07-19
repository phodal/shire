package com.phodal.shirelang.run.executor

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.phodal.shire.agent.CustomAgentSseExecutor
import com.phodal.shirecore.agent.CustomAgent
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.run.flow.ShireConversationService
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.config.interaction.PostFunction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CustomRemoteAgentLlmExecutor(
    override val context: ShireLlmExecutorContext,
    private val agent: CustomAgent,
) : ShireLlmExecutor(context) {
    override fun execute(postFunction: PostFunction) {
        ApplicationManager.getApplication().invokeLater {
            val stringFlow: Flow<String>? = CustomAgentSseExecutor(project = context.myProject).execute(context.prompt, agent)

            if (stringFlow == null) {
                context.console.print(ShireBundle.message("shire.llm.notfound"), ConsoleViewContentType.ERROR_OUTPUT)
                context.processHandler.detachProcess()
                postFunction(null, null)
                return@invokeLater
            }

            ShireCoroutineScope.scope(context.myProject).launch {
                val llmResult = StringBuilder()
                runBlocking {
                    stringFlow.collect {
                        llmResult.append(it)
                        context.console.print(it, ConsoleViewContentType.NORMAL_OUTPUT)
                    }
                }

                context.console.print("\nDone!", ConsoleViewContentType.SYSTEM_OUTPUT)
                val llmResponse = llmResult.toString()
                context.myProject.getService(ShireConversationService::class.java)
                    .refreshLlmResponseCache(context.configuration.getScriptPath(), llmResponse)

                postFunction(llmResponse, null)
                context.processHandler.detachProcess()
            }
        }
    }
}