package com.phodal.shirelang.run.flow

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.compiler.ShireParsedResult
import com.phodal.shirelang.run.ShireConsoleView
import kotlinx.coroutines.runBlocking

@Service(Service.Level.PROJECT)
class ShireConversationService(val project: Project) {
    private val cachedConversations: MutableMap<String, ShireProcessContext> = mutableMapOf()

    fun createConversation(scriptPath: String, result: ShireParsedResult): ShireProcessContext {
        val conversation = ShireProcessContext(scriptPath, result, "", "")
        cachedConversations[scriptPath] = conversation
        return conversation
    }

    fun getConversation(scriptPath: String): ShireProcessContext? {
        return cachedConversations[scriptPath]
    }

    fun getLlmResponse(scriptPath: String): String {
        return cachedConversations[scriptPath]?.llmResponse ?: ""
    }

    /**
     * Updates the LLM response for a given script path in the cached conversations.
     * If the script path exists in the cached conversations, the LLM response is updated with the provided value.
     *
     * @param scriptPath The script path for which the LLM response needs to be updated.
     * @param llmResponse The new LLM response to be updated for the given script path.
     */
    fun refreshLlmResponseCache(scriptPath: String, llmResponse: String) {
        cachedConversations[scriptPath]?.let {
            cachedConversations[scriptPath] = it.copy(llmResponse = llmResponse)
        }
    }

    /**
     * Updates the IDE output for a conversation at the specified path.
     *
     * @param path The path of the conversation to update.
     * @param ideOutput The new IDE output to set for the conversation.
     */
    fun refreshIdeOutput(path: String, ideOutput: String) {
        cachedConversations[path]?.let {
            cachedConversations[path] = it.copy(ideOutput = ideOutput)
        }
    }

    /**
     * Function to try re-running a conversation script.
     *
     * @param scriptPath The path of the script to re-run
     */
    fun retryScriptExecution(scriptPath: String, consoleView: ShireConsoleView?) {
        if (cachedConversations.isEmpty()) return
        val conversation = cachedConversations[scriptPath] ?: return
        if (conversation.alreadyReRun) return
        conversation.alreadyReRun = true

        val prompt = StringBuilder()
        val compiledResult = conversation.compiledResult
        if (compiledResult.isLocalCommand) {
            val message =
                ShireBundle.message("shire.prompt.fix.command", compiledResult.sourceCode, compiledResult.shireOutput)
            prompt.append(message)
        }

        prompt.append(ShireBundle.message("shire.prompt.fix.run-result", conversation.ideOutput))

        val finalPrompt = prompt.toString()
        if (consoleView != null) {
            runBlocking {
                try {
                    LlmProvider.provider(project)
                        ?.stream(finalPrompt, "", true)
                        ?.collect {
                            consoleView.print(it, ConsoleViewContentType.NORMAL_OUTPUT)
                        }
                } catch (e: Exception) {
                    consoleView.print(e.message ?: "Error", ConsoleViewContentType.ERROR_OUTPUT)
                }
            }
        }
    }
}
