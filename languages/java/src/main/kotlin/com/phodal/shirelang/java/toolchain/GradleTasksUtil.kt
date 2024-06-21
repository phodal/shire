package com.phodal.shirelang.java.toolchain

import com.intellij.execution.RunManager
import com.intellij.openapi.externalSystem.service.ui.completion.TextCompletionInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.phodal.shirelang.java.impl.GRADLE_COMPLETION_COMPARATOR
import org.jetbrains.plugins.gradle.service.execution.GradleExternalTaskConfigurationType
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration
import org.jetbrains.plugins.gradle.service.project.GradleTasksIndices
import org.jetbrains.plugins.gradle.util.GradleTaskData

object GradleTasksUtil {
    fun collectGradleTasks(project: Project): List<TextCompletionInfo> {
        val indices = GradleTasksIndices.getInstance(project)

        val tasks = indices.findTasks(project.guessProjectDir()!!.path)
            .filterNot { it.isInherited }
            .groupBy { it.name }
            .map { TextCompletionInfo(it.key, it.value.first().description) }
            .sortedWith(Comparator.comparing({ it.text }, GRADLE_COMPLETION_COMPARATOR))

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
}
