package com.phodal.shirecore.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project

interface ProjectRunService {
    fun isAvailable(project: Project): Boolean

    fun run(project: Project, taskName: String)

    /**
     * Return List of available tasks
     */
    fun lookupAvailableTask(
        project: Project,
        parameters: CompletionParameters,
        result: CompletionResultSet,
    ): List<LookupElement> {
        return emptyList()
    }

    companion object {
        val EP_NAME = ExtensionPointName<ProjectRunService>("com.phodal.shireRunProjectService")

        fun all(): List<ProjectRunService> {
            return EP_NAME.extensionList
        }

        fun provider(project: Project): ProjectRunService? {
            val projectRunServices = EP_NAME.extensionList
            return projectRunServices.firstOrNull { it.isAvailable(project) }
        }
    }
}
