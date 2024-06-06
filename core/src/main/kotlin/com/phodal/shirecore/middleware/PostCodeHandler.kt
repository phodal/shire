package com.phodal.shirecore.middleware

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement

interface PostProcessor {
    fun isApplicable(context: PostCodeHandleContext): Boolean

    /**
     * Some init tasks, like metric for time, etc.
     */
    fun setup(context: PostCodeHandleContext): String

    fun execute(project: Project, context: PostCodeHandleContext, genText: String): String

    /**
     * Clean up tasks, like metric for time, etc.
     */
    fun finish(context: PostCodeHandleContext): String

    companion object {
        private val EP_NAME: ExtensionPointName<PostProcessor> =
            ExtensionPointName.create("com.phodal.shirePostProcessor")

        fun handler(handleName: String, context: PostCodeHandleContext): List<PostProcessor> {
            // check from builtin
            val builtinHandler = BuiltinPostHandler.values().find {
                it.handleName == handleName
            }

//            if (builtinHandler != null) {
//                return listOf(BuiltinPostProcessor(builtinHandler))
//            }

            return EP_NAME.extensionList.filter {
                it.isApplicable(context)
            }
        }

    }
}

data class PostCodeHandleContext (
    val element: PsiElement,
    val file: VirtualFile
)

/**
 * Post middleware actions, like
 * Logging, Metrics, CodeVerify, RunCode, ParseCode etc.
 *
 */
enum class BuiltinPostHandler(var handleName: String) {
    /**
     * Logging the action.
     */
    Logging("logging"),

    /**
     * Metric time spent on the action.
     */
    TimeMetric("timeMetric"),

    /**
     * Acceptance metric.
     */
    AcceptanceMetric("acceptanceMetric"),

    /**
     * Check has code error or PSI issue.
     */
    CodeVerify("codeVerify"),

    /**
     * Run generate text code
     */
    RunCode("runCode"),

    /**
     * Parse text to code blocks
     */
    ParseCode("parseCode"),

    /**
     * For example, TestCode should be in the correct directory, like java test should be in test directory.
     */
    InferCodeLocation("InferCodeLocation"),
}