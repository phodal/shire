package com.phodal.shirecore.llm

import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.ProjectScope
import com.intellij.util.indexing.FileBasedIndex
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.index.MODEL_LIST
import com.phodal.shirecore.index.MODEL_TITLE
import com.phodal.shirecore.index.SHIRE_ENV_ID
import com.phodal.shirecore.index.valueAsString
import com.phodal.shirecore.middleware.PostProcessorContext
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
     * @return A Flow of String values representing the chat completion responses.
     */
    fun stream(promptText: String, systemPrompt: String, keepHistory: Boolean = true): Flow<String>

    /**
     * config LLM Provider from [PostProcessorContext]
     */
    fun configRunLlm(): LlmConfig? {
        if (project == null) {
            return null
        }

        val modelName = PostProcessorContext.getData()?.llmModelName ?: return null
        val scope = ProjectScope.getContentScope(project!!)

        val jsonFile = runReadAction {
            FileBasedIndex.getInstance().getContainingFiles(SHIRE_ENV_ID, MODEL_LIST, scope)
                .firstOrNull()
                ?.let {
                    (PsiManager.getInstance(project!!).findFile(it) as? JsonFile)
                }
        }

        val modelConfig = getModelConfig(modelName, jsonFile)
        if (modelConfig != null) {
            return LlmConfig.fromJson(modelConfig)
        }

        return null
    }

    fun getModelConfig(modelName: String, psiFile: JsonFile?): JsonObject? {
        val rootObject = psiFile?.topLevelValue as? JsonObject ?: return null
        val envObject = rootObject.propertyList.firstOrNull { it.name == MODEL_LIST }?.value as? JsonArray
        return envObject?.children?.firstOrNull {
            it is JsonObject && it.findProperty(MODEL_TITLE)?.valueAsString(it) == modelName
        } as? JsonObject
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
