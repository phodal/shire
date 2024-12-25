package com.phodal.shirelang.run.executor

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.config.InteractionType
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.config.interaction.PostFunction
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.provider.ide.LocationInteractionContext
import com.phodal.shirecore.provider.ide.LocationInteractionProvider
import com.phodal.shirecore.runner.console.cancelWithConsole
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.run.flow.ShireConversationService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ShireDefaultLlmExecutor(
    override val context: ShireLlmExecutorContext,
    private val isLocalMode: Boolean,
) : ShireLlmExecutor(context) {
    override fun execute(postFunction: PostFunction) {
        ApplicationManager.getApplication().invokeLater({
            val console = context.console
            if (isLocalMode && context.hole == null) {
                console?.print(ShireBundle.message("shire.run.local.mode"), ConsoleViewContentType.SYSTEM_OUTPUT)
                context.processHandler.detachProcess()
                return@invokeLater
            }

            val interaction = context.hole?.interaction
            val interactionContext = LocationInteractionContext(
                location = context.hole?.actionLocation ?: ShireActionLocation.RUN_PANEL,
                interactionType = interaction ?: InteractionType.AppendCursorStream,
                editor = context.editor,
                project = context.myProject,
                prompt = context.prompt,
                console = console,
            )

            if (interaction != null) {
                if (context.hole!!.interaction == InteractionType.OnPaste) {
                    return@invokeLater
                }
                val interactionProvider = LocationInteractionProvider.provide(interactionContext)
                if (interactionProvider != null) {
                    interactionProvider.execute(interactionContext) { response, textRange ->
                        postFunction(response, textRange)
                        try {
                            context.processHandler.detachProcess()
                        } catch (e: Exception) {
                            console?.print(e.message ?: "Error", ConsoleViewContentType.ERROR_OUTPUT)
                        }
                    }

                    return@invokeLater
                }
            }

            ShireCoroutineScope.scope(context.myProject).launch {
                val llmResult = StringBuilder()
                runBlocking {
                    try {
                        LlmProvider.provider(context.myProject)?.stream(context.prompt, "", false)
                            ?.cancelWithConsole(console)?.collect {
                            llmResult.append(it)
                            console?.print(it, ConsoleViewContentType.NORMAL_OUTPUT)
                        } ?: console?.print(
                            "DefaultLlm" + ShireBundle.message("shire.llm.notfound"),
                            ConsoleViewContentType.ERROR_OUTPUT
                        )
                    } catch (e: Exception) {
                        console?.print(e.message ?: "Error", ConsoleViewContentType.ERROR_OUTPUT)
                        context.processHandler.detachProcess()
                    }
                }

                console?.print(ShireBundle.message("shire.llm.done"), ConsoleViewContentType.SYSTEM_OUTPUT)

                val response = llmResult.toString()
                context.myProject.getService(ShireConversationService::class.java)
                    .refreshLlmResponseCache(context.configuration.getScriptPath(), response)

                postFunction(response, null)
                context.processHandler.detachProcess()
            }
        }, ModalityState.nonModal())
    }
}

