package com.phodal.shirelang.java.toolchain

import com.intellij.openapi.externalSystem.model.project.LibraryData
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.gradle.util.GradleConstants

data class CommonLibraryData(val groupId: String?, val artifactId: String?, val version: String?)

object JavaLibraryConverter {
    fun prepareLibraryData(project: Project): List<CommonLibraryData>? {
        return prepareGradleLibrary(project) ?: MavenBuildTool().prepareLibraryData(project)
    }

    fun prepareGradleLibrary(project: Project): List<CommonLibraryData>? {
        val basePath = project.basePath ?: return null
        val projectData = ProjectDataManager.getInstance().getExternalProjectData(
            project, GradleConstants.SYSTEM_ID, basePath
        )

        val libraryDataList: List<LibraryData>? = projectData?.externalProjectStructure?.children?.filter {
            it.data is LibraryData
        }?.map {
            it.data as LibraryData
        }

        // to SimpleLibraryData

        return libraryDataList?.map {
            CommonLibraryData(it.groupId, it.artifactId, it.version)
        }
    }
}