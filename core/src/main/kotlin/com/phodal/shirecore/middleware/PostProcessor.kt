package com.phodal.shirecore.middleware

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project

data class PostProcessorNode(
    val funName: String,
    val args: List<Any>,
)

interface PostProcessor {
    val processorName: String

    /**
     * This function checks if a given context is applicable for handling post codes.
     *
     * @param context the PostCodeHandleContext to be checked for applicability
     * @return true if the context is applicable for handling post codes, false otherwise
     */
    fun isApplicable(context: PostCodeHandleContext): Boolean

    /**
     * Some init tasks, like metric for time, etc.
     */
    fun setup(context: PostCodeHandleContext): String

    /**
     * Executes a function with the given project, context, and generated text.
     *
     * @param project the project to execute the function on
     * @param context the context in which the function is executed
     * @param genText the generated text to be used in the execution
     * @return a string result of the execution
     */
    fun execute(project: Project, context: PostCodeHandleContext): String

    /**
     * Clean up tasks, like metric for time, etc.
     */
    fun finish(context: PostCodeHandleContext): String

    companion object {
        private val EP_NAME: ExtensionPointName<PostProcessor> =
            ExtensionPointName.create("com.phodal.shirePostProcessor")

        fun handler(handleName: String): PostProcessor? {
            return EP_NAME.extensionList.find {
                it.processorName == handleName
            }
        }

        fun allNames(): List<String> {
            return EP_NAME.extensionList.map { it.processorName }
        }

        fun execute(project: Project, funcNode: List<PostProcessorNode>, handleContext: PostCodeHandleContext) {
            funcNode.forEach {
                val handler = handler(it.funName)
                if (handler != null) {
                    handler.setup(handleContext)
                    handler.execute(project, handleContext)
                    handler.finish(handleContext)
                }
            }
        }
    }
}
