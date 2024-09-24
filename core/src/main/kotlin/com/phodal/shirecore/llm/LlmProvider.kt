package com.phodal.shirecore.llm

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.psi.search.ProjectScope
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.middleware.PostProcessorContext
import com.phodal.shire.json.llm.LlmEnv
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
    var project: Project?

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
     * Streams chat completion responses from the service.
     *
     * @param promptText The text prompt to send to the service.
     * @param systemPrompt The system prompt to send to the service.
     * @param keepHistory Flag indicating whether to keep the chat history.
     * @param llmConfig A default llmConfig, if not provided it will be read from the project settings.
     * @return A Flow of String values representing the chat completion responses.
     */
    fun stream(promptText: String, systemPrompt: String, keepHistory: Boolean = true, llmConfig: LlmConfig? = null): Flow<String>

    /**
     * config LLM Provider from [PostProcessorContext]
     */
    fun configRunLlm(): LlmConfig? {
        if (project == null) return null

        val modelName = PostProcessorContext.getData()?.llmModelName ?: return null
        val scope = ProjectScope.getContentScope(project!!)


        val modelConfig = LlmEnv.configFromFile(modelName, scope, project!!)
        if (modelConfig != null) {
            return LlmConfig.fromJson(modelConfig)
        }

        return null
    }

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
                ShirelangNotifications.error(project, ShireCoreBundle.message("shire.llm.notfound"))
                null
            } else {
                providers.first()
            }
        }
    }
}
