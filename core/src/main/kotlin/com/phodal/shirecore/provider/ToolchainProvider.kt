package com.phodal.shirecore.provider

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import kotlin.reflect.KClass

class ToolchainContextItem(
    val clazz: KClass<*>,
    var text: String
)

data class ToolchainPrepareContext(
    val sourceFile: PsiFile?,
    val element: PsiElement?,
    val extraItems: List<ToolchainContextItem> = emptyList(),
)

interface ToolchainProvider {
    fun isApplicable(project: Project, context: ToolchainPrepareContext): Boolean

    @RequiresBackgroundThread
    suspend fun collect(project: Project, context: ToolchainPrepareContext): List<ToolchainContextItem>

    companion object {
        private val EP_NAME = ExtensionPointName<ToolchainProvider>("com.phodal.shireToolchainProvider")

        suspend fun gatherToolchainContextItems(
            project: Project,
            toolchainPrepareContext: ToolchainPrepareContext,
        ): List<ToolchainContextItem> {
            val elements = mutableListOf<ToolchainContextItem>()

            val chatContextProviders = EP_NAME.extensionList
            for (provider in chatContextProviders) {
                try {
                    val applicable = provider.isApplicable(project, toolchainPrepareContext)
                    if (applicable) {
                        elements.addAll(provider.collect(project, toolchainPrepareContext))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            elements.addAll(toolchainPrepareContext.extraItems)
            return elements.distinctBy { it.text }
        }
    }
}