package com.phodal.shirelang.run.runner

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.provider.ide.LocationInteractionContext
import com.phodal.shirecore.provider.ide.LocationInteractionProvider
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.run.flow.ShireConversationService
import kotlinx.coroutines.*

class ShireDefaultRunner(
    override val context: ShireRunnerContext,
    private val isLocalMode: Boolean,
) : ShireRunner(context) {
    override fun execute(postFunction: (response: String) -> Unit) {
        ApplicationManager.getApplication().invokeLater({
            if (isLocalMode) {
                context.console.print(ShireBundle.message("shire.run.local.mode"), ConsoleViewContentType.SYSTEM_OUTPUT)
                context.processHandler.detachProcess()
                return@invokeLater
            }

            val interactionContext = LocationInteractionContext(
                location = context.hole?.actionLocation ?: ShireActionLocation.INTENTION_MENU,
                interactionType = context.hole?.interaction ?: InteractionType.AppendCursorStream,
                editor = context.editor,
                project = context.myProject,
                prompt = context.prompt,
                console = context.console,
            )

            if (context.hole?.interaction != null) {
                val interactionProvider = LocationInteractionProvider.provide(interactionContext)
                if (interactionProvider != null) {
                    interactionProvider.execute(interactionContext) {
                        postFunction(it)
                        try {
                            context.processHandler.detachProcess()
                        } catch (e: Exception) {
                            context.console.print(e.message ?: "Error", ConsoleViewContentType.ERROR_OUTPUT)
                        }
                    }
                    return@invokeLater
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                val llmResult = StringBuilder()
                runBlocking {
                    LlmProvider.provider(context.myProject)?.stream(context.prompt, "", false)?.collect {
                        llmResult.append(it)

                        context.console.print(it, ConsoleViewContentType.NORMAL_OUTPUT)
                    } ?: context.console.print(
                        ShireBundle.message("shire.llm.notfound"),
                        ConsoleViewContentType.ERROR_OUTPUT
                    )
                }

                context.console.print(ShireBundle.message("shire.llm.done"), ConsoleViewContentType.SYSTEM_OUTPUT)

                val response = llmResult.toString()
                context.myProject.getService(ShireConversationService::class.java)
                    .refreshLlmResponseCache(context.configuration.getScriptPath(), response)

                postFunction(response)
                context.processHandler.detachProcess()
            }
        }, ModalityState.NON_MODAL)
    }
}

