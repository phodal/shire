package com.phodal.shirelang.java.toolchain

import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.openapi.externalSystem.model.project.LibraryData
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.externalSystem.service.ui.completion.TextCompletionInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.plugins.gradle.util.GradleConstants

class GradleBuildTool: BuildTool {
    override fun prepareLibraryData(project: Project): List<CommonLibraryData>? {
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

    override fun collectTasks(project: Project): List<TextCompletionInfo> {
        TODO("Not yet implemented")
    }

    override fun configureRun(
        project: Project,
        virtualFile: VirtualFile,
        taskName: String,
    ): LocatableConfigurationBase<*>? {
        TODO("Not yet implemented")
    }
}