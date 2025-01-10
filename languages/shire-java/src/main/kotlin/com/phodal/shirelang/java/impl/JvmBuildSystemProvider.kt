package com.phodal.shirelang.java.impl

import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.project.Project
import com.phodal.shirecore.variable.toolchain.buildsystem.BuildSystemContext
import com.phodal.shirecore.provider.context.BuildSystemProvider
import com.phodal.shirelang.java.toolchain.*
import org.jetbrains.plugins.gradle.util.GradleConstants

open class JvmBuildSystemProvider : BuildSystemProvider() {
    override fun collect(project: Project): BuildSystemContext {
        val projectDataManager = ProjectDataManager.getInstance()
        val javaVersion = JvmLanguageDetector.detectLanguageLevel(project, null)
        val gradleInfo = projectDataManager.getExternalProjectsData(project, GradleConstants.SYSTEM_ID)

        when {
            gradleInfo.isNotEmpty() -> {
                val buildTool = GradleBuildTool()
                return BuildSystemContext(
                    buildToolName = buildTool.toolName(),
                    buildToolVersion = "",
                    languageName = "Java",
                    languageVersion = "$javaVersion",
                    taskString = buildTool.collectTasks(project).joinToString(" ") { it.text },
                    libraries = buildTool.prepareLibraryData(project)?.map {
                        it.prettyString()
                    }  ?: emptyList()
                )
            }

            else -> {
                val buildTool = MavenBuildTool()
                val buildToolName = buildTool.toolName()
                val libraryData = buildTool.prepareLibraryData(project)
                val collectTasks = buildTool.collectTasks(project)
                val taskString = collectTasks.joinToString(" ") { it.text }

                return BuildSystemContext(
                    buildToolName = buildToolName,
                    buildToolVersion = "",
                    languageName = "Java",
                    languageVersion = "$javaVersion",
                    taskString = taskString,
                    libraries = libraryData.map {
                        it.prettyString()
                    }
                )
            }
        }
    }

}

val JAVA_TASK_COMPLETION_COMPARATOR = Comparator<String> { o1, o2 ->
    when {
        o1.startsWith("--") && o2.startsWith("--") -> o1.compareTo(o2)
        o1.startsWith("-") && o2.startsWith("--") -> -1
        o1.startsWith("--") && o2.startsWith("-") -> 1
        o1.startsWith(":") && o2.startsWith(":") -> o1.compareTo(o2)
        o1.startsWith(":") && o2.startsWith("-") -> -1
        o1.startsWith("-") && o2.startsWith(":") -> 1
        o2.startsWith("-") -> -1
        o2.startsWith(":") -> -1
        o1.startsWith("-") -> 1
        o1.startsWith(":") -> 1
        else -> o1.compareTo(o2)
    }
}