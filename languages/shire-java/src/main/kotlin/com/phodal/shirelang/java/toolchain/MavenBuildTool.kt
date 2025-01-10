package com.phodal.shirelang.java.toolchain

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.externalSystem.service.ui.completion.TextCompletionInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.provider.context.BuildTool
import com.phodal.shirecore.provider.context.CommonLibraryData
import com.phodal.shirelang.java.impl.JAVA_TASK_COMPLETION_COMPARATOR
import org.jetbrains.idea.maven.execution.MavenRunConfiguration
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType
import org.jetbrains.idea.maven.execution.MavenRunnerParameters
import org.jetbrains.idea.maven.project.MavenProject
import org.jetbrains.idea.maven.project.MavenProjectsManager

class MavenBuildTool() : BuildTool {
    override fun toolName(): String = "Maven"

    override fun prepareLibraryData(project: Project): List<CommonLibraryData> {
        val projectDependencies: List<org.jetbrains.idea.maven.model.MavenArtifact> =
            MavenProjectsManager.getInstance(project).projects.flatMap {
                it.dependencies
            }

        return projectDependencies.map {
            CommonLibraryData(it.groupId, it.artifactId, it.version)
        }
    }

    override fun collectTasks(project: Project): List<TextCompletionInfo> {
        val projectsManager = MavenProjectsManager.getInstance(project)
        val mavenProjects: List<MavenProject> = projectsManager.projects
        val tasks = mavenProjects.flatMap { it.plugins }.flatMap { it.executions }
            .map { TextCompletionInfo(it.executionId, it.phase) }
            .sortedWith(Comparator.comparing({ it.text }, JAVA_TASK_COMPLETION_COMPARATOR))

        return tasks
    }

    override fun configureRun(
        project: Project,
        taskName: String,
        virtualFile: VirtualFile?,
    ): LocatableConfigurationBase<*>? {
        if (virtualFile == null) return null

        val projectsManager = MavenProjectsManager.getInstance(project);

        val mavenProject: MavenProject = projectsManager.findProject(virtualFile) ?: return null
        val module = runReadAction { projectsManager.findModule(mavenProject) } ?: return null

        var trulyMavenProject = projectsManager.projects.firstOrNull {
            it.mavenId.artifactId == module.name
        }

        if (trulyMavenProject == null) {
            trulyMavenProject = projectsManager.projects.first() ?: return null
        }

        val pomFile = trulyMavenProject.file.name

        val parameters = MavenRunnerParameters(
            true, trulyMavenProject.directory, pomFile, listOf("test"),
            projectsManager.explicitProfiles.enabledProfiles, arrayListOf()
        )

        // $MODULE_WORKING_DIR$
        //
        // -ea Method: com.example.demo.MathHelperTest should_ReturnSum_When_GivenTwoPositiveNumbers
        // /Users/phodal/Library/Java/JavaVirtualMachines/corretto-18.0.2/Contents/Home/bin/java
        // -ea -Didea.test.cyclic.buffer.size=1048576
        // -javaagent:ideaIU-2024.1/lib/idea_rt.jar=54637:1/bin -Dfile.encoding=UTF-8
        // -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8

        val runnerAndConfigurationSettings =
            MavenRunConfigurationType.createRunnerAndConfigurationSettings(null, null, parameters, project)

        val runManager = RunManager.getInstance(project)

        val configuration = runnerAndConfigurationSettings.configuration

        runManager.addConfiguration(runnerAndConfigurationSettings)
        runManager.selectedConfiguration = runnerAndConfigurationSettings

        return configuration as MavenRunConfiguration
    }

}