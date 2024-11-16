package com.phodal.shirecore.middleware

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project

data class PostProcessorFuncSign(
    val funcName: String,
    val args: List<Any>,
)

/**
 * The PostProcessor interface defines a contract for post-processing tasks. Implementations of this
 * interface are expected to handle specific post-processing operations, which are identified by a
 * unique name.
 *
 * The interface provides methods to check the applicability of a given context for handling post codes,
 * to set up any necessary initial tasks, to execute the post-processing function, and to perform clean
 * up tasks after execution.
 *
 *
 * @property processorName the unique name of the post-processing function, the built-in functions are defined in [PostProcessorType]
 */
interface PostProcessor {
    val processorName: String
    val description: String

    /**
     * This function checks if a given context is applicable for handling post codes.
     *
     * @param context the PostCodeHandleContext to be checked for applicability
     * @return true if the context is applicable for handling post codes, false otherwise
     */
    fun isApplicable(context: PostProcessorContext): Boolean

    /**
     * Some init tasks, like metric for time, etc.
     */
    fun setup(context: PostProcessorContext): Any {
        context.changeCount = 0
        context.lineCount = 0
        context.complexityCount = 0
        return ""
    }

    /**
     * Executes a function with the given project, context, and generated text.
     *
     * @param project the project to execute the function on
     * @param context the context in which the function is executed
     * @param genText the generated text to be used in the execution
     * @return a string result of the execution
     */
    fun execute(project: Project, context: PostProcessorContext, console: ConsoleView?, args: List<Any>): Any

    /**
     * Clean up tasks, like metric for time, etc.
     */
    fun finish(context: PostProcessorContext): Any? {
        // Implement logic to finalize changeCount, lineCount, and complexityCount metrics
        return ""
    }

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

        fun all(): List<PostProcessor> {
            return EP_NAME.extensionList
        }
    }
}
