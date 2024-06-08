package com.phodal.shirelang.java.toolchain

import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.externalSystem.service.ui.completion.TextCompletionInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.phodal.shirelang.java.impl.GRADLE_COMPLETION_COMPARATOR
import org.jetbrains.plugins.gradle.service.project.GradleTasksIndices
import org.jetbrains.plugins.gradle.util.GradleConstants

object GradleTasksUtil {
    fun collectGradleTasksWithCheck(project: Project): List<TextCompletionInfo> {
        val projectDataManager = ProjectDataManager.getInstance()
        val projectsData = projectDataManager.getExternalProjectsData(project, GradleConstants.SYSTEM_ID)

        return if (projectsData.isNotEmpty()) {
            collectGradleTasks(project)
        } else {
            emptyList()
        }
    }

    /**
     * Check start java task name, like:
     * - Spring Boot: `bootRun`
     * - Quarkus: `quarkusDev`
     * - Micronaut: `run`
     * - Helidon: `run`
     */
    fun getRunTaskName(project: Project): String {
        val tasks = collectGradleTasks(project)
        val runTasks = tasks.filter { it.text.contains("run", ignoreCase = true) }
        if (runTasks.isNotEmpty()) {
            return runTasks.first().text
        }

        return "run"
    }

    fun collectGradleTasks(project: Project): List<TextCompletionInfo> {
        val indices = GradleTasksIndices.getInstance(project)

        val tasks = indices.findTasks(project.guessProjectDir()!!.path)
            .filterNot { it.isInherited }
            .groupBy { it.name }
            .map { TextCompletionInfo(it.key, it.value.first().description) }
            .sortedWith(Comparator.comparing({ it.text }, GRADLE_COMPLETION_COMPARATOR))
        return tasks
    }
}
