package com.phodal.shirecore.llm

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.phodal.shirecore.ShirelangNotifications
import kotlinx.coroutines.flow.Flow

/**
 * Interface for providing LLM (Language Model) services.
 * This interface defines methods for interacting with LLM services, such as checking applicability, sending prompts,
 * streaming chat completion responses, and clearing messages.
 *
 * Implementations of this interface should provide functionality for interacting with specific LLM services.
 *
 */
interface LlmProvider {
    /**
     * Default timeout for the provider.
     * This is used to set the default timeout for the provider.
     * For example, If you want to wait in 10min, you can use:
     * ```Kotlin
     * Duration.ofSeconds(defaultTimeout)
     * ```
     */
    val defaultTimeout: Long get() = 600

    /**
     * Checks if the given project is applicable for some operation.
     *
     * @param project the project to check for applicability
     * @return true if the project is applicable, false otherwise
     */
    fun isApplicable(project: Project): Boolean

    /**
     * Call LLM and return Text object as response.
     *
     * @param promptText the text to be displayed as a prompt
     * @return the String value entered by the user
     */
    fun text(promptText: String): String


    /**
     * Streams chat completion responses from the service.
     *
     * @param promptText The text prompt to send to the service.
     * @param systemPrompt The system prompt to send to the service.
     * @param keepHistory Flag indicating whether to keep the chat history.
     * @return A Flow of String values representing the chat completion responses.
     */
    fun stream(promptText: String, systemPrompt: String, keepHistory: Boolean = true): Flow<String>

    /**
     * Clears the message displayed in the UI.
     */
    fun clearMessage()

    companion object {
        private val EP_NAME: ExtensionPointName<LlmProvider> =
            ExtensionPointName.create("com.phodal.shireLlmProvider")

        /**
         * Returns an instance of LlmProvider based on the given Project.
         *
         * @param project the Project for which to find a suitable LlmProvider
         * @return an instance of LlmProvider if a suitable provider is found, null otherwise
         */
        fun provider(project: Project): LlmProvider? {
            val providers = EP_NAME.extensions.filter { it.isApplicable(project) }
            return if (providers.isEmpty()) {
                ShirelangNotifications.info(project, "No LLM provider found")
                null
            } else {
                providers.first()
            }
        }
    }
}
