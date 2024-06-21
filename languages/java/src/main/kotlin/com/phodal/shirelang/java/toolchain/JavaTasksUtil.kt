package com.phodal.shirelang.java.toolchain

import com.intellij.execution.RunManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.externalSystem.service.ui.completion.TextCompletionInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirelang.java.impl.JAVA_TASK_COMPLETION_COMPARATOR
import org.jetbrains.idea.maven.execution.MavenRunConfiguration
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType
import org.jetbrains.idea.maven.execution.MavenRunnerParameters
import org.jetbrains.idea.maven.project.MavenProject
import org.jetbrains.idea.maven.project.MavenProjectsManager
import org.jetbrains.plugins.gradle.service.execution.GradleExternalTaskConfigurationType
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration
import org.jetbrains.plugins.gradle.service.project.GradleTasksIndices
import org.jetbrains.plugins.gradle.util.GradleTaskData

object JavaTasksUtil {
    fun collectGradleTasks(project: Project): List<TextCompletionInfo> {
        val indices = GradleTasksIndices.getInstance(project)

        val tasks = indices.findTasks(project.guessProjectDir()!!.path)
            .filterNot { it.isInherited }
            .groupBy { it.name }
            .map { TextCompletionInfo(it.key, it.value.first().description) }
            .sortedWith(Comparator.comparing({ it.text }, JAVA_TASK_COMPLETION_COMPARATOR))

        return tasks
    }

    fun collectGradleTasksData(project: Project): List<GradleTaskData> {
        val indices = GradleTasksIndices.getInstance(project)
        val tasks = indices.findTasks(project.guessProjectDir()!!.path)
        return tasks
    }

    /**
     * This function is used to create a Gradle run configuration for a specific virtual file in a project.
     * It takes the virtual file, project, and task name as parameters and returns a GradleRunConfiguration object.
     *
     * @param project The project in which the configuration is being created.
     * @param taskName The name of the task to be executed in the Gradle run configuration.
     * @return A GradleRunConfiguration object representing the created configuration.
     */
    fun configureGradleRun(project: Project, taskName: String): GradleRunConfiguration {
        val runManager = RunManager.getInstance(project)
        val configuration = runManager.createConfiguration(
            taskName,
            GradleExternalTaskConfigurationType::class.java
        )
        val runConfiguration = configuration.configuration as GradleRunConfiguration

        runConfiguration.isDebugServerProcess = false
        runConfiguration.settings.externalProjectPath = project.guessProjectDir()?.path

        runConfiguration.rawCommandLine = taskName

        runManager.addConfiguration(configuration)
        runManager.selectedConfiguration = configuration

        return runConfiguration
    }

    fun createConfigForMaven(virtualFile: VirtualFile, project: Project): MavenRunConfiguration? {
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

    fun collectMavenTasks(project: Project): List<TextCompletionInfo> {
        val projectsManager = MavenProjectsManager.getInstance(project)
        val mavenProjects: List<MavenProject> = projectsManager.projects
        val tasks = mavenProjects.flatMap { it.plugins }.flatMap { it.executions }
            .map { TextCompletionInfo(it.executionId, it.phase) }
            .sortedWith(Comparator.comparing({ it.text }, JAVA_TASK_COMPLETION_COMPARATOR))

        return tasks
    }
}