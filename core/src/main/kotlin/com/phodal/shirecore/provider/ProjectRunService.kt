package com.phodal.shirecore.provider

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project

interface ProjectRunService {
    fun run(project: Project)

    companion object {
        val EP_NAME = ExtensionPointName<ProjectRunService>("com.phodal.shireRunProjectService")

        fun runProject(project: Project) {
            val projectRunServices = EP_NAME.extensionList
            for (provider in projectRunServices) {
                try {
                    provider.run(project)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
