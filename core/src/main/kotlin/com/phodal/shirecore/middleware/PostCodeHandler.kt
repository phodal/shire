package com.phodal.shirecore.middleware

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.AutoTesting

interface PostCodeHandler {
    fun isApplicable(context: PostCodeHandleContext): Boolean

    /**
     * Some init tasks, like metric for time, etc.
     */
    fun initTask(context: PostCodeHandleContext): String

    fun execute(project: Project, context: PostCodeHandleContext, genText: String): String

    companion object {
        private val EP_NAME: ExtensionPointName<AutoTesting> =
            ExtensionPointName.create("com.phodal.shirePostCodeHandler")


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
enum class PostCodeHandle {
    /**
     * Logging the action.
     */
    Logging,

    /**
     * Metric time spent on the action.
     */
    TimeMetric,

    /**
     * Acceptance metric.
     */
    AcceptanceMetric,

    /**
     * Check has code error or PSI issue.
     */
    CodeVerify,

    /**
     * Run generate text code
     */
    RunCode,

    /**
     * Parse text to code blocks
     */
    ParseCode,

    /**
     * For example, TestCode should be in the correct directory, like java test should be in test directory.
     */
    InferenceCodeLocation
}