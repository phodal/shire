package com.phodal.shirecore.provider

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project

interface ProjectRunService {
    fun isAvailable(project: Project): Boolean

    fun run(project: Project, taskName: String?)

    /**
     * Return List of available tasks
     */
    fun taskCompletion(project: Project) {
        // do nothing
    }

    companion object {
        val EP_NAME = ExtensionPointName<ProjectRunService>("com.phodal.shireRunProjectService")

        fun provider(project: Project): ProjectRunService? {
            val projectRunServices = EP_NAME.extensionList
            return projectRunServices.firstOrNull { it.isAvailable(project) }
        }
    }
}
