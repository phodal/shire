package com.phodal.shirecore.middleware

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.AutoTesting

interface PostCodeHandler {
    fun isApplicable(context: PostCodeHandleContext): Boolean

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
    Logging, Metrics, CodeVerify, RunCode, ParseCode
}