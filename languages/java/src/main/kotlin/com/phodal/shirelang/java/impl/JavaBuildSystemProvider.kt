package com.phodal.shirelang.java.impl

import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.externalSystem.service.ui.completion.TextCompletionInfo
import com.intellij.openapi.module.LanguageLevelUtil
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.projectRoots.JavaSdkType
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.pom.java.LanguageLevel
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtil
import com.phodal.shirecore.buildsystem.BuildSystemContext
import com.phodal.shirecore.provider.BuildSystemProvider
import org.jetbrains.plugins.gradle.service.project.GradleTasksIndices
import org.jetbrains.plugins.gradle.util.GradleConstants

open class JavaBuildSystemProvider : BuildSystemProvider() {
    override fun collect(project: Project): BuildSystemContext {
        val projectDataManager = ProjectDataManager.getInstance()
        val buildToolName: String
        var taskString = ""

        val gradleInfo = projectDataManager.getExternalProjectsData(project, GradleConstants.SYSTEM_ID)
        if (gradleInfo.isNotEmpty()) {
            buildToolName = "Gradle"
            val indices = GradleTasksIndices.getInstance(project)

            val tasks = indices.findTasks(project.guessProjectDir()!!.path)
                .filterNot { it.isInherited }
                .groupBy { it.name }
                .map { TextCompletionInfo(it.key, it.value.first().description) }
                .sortedWith(Comparator.comparing({ it.text }, GRADLE_COMPLETION_COMPARATOR))

            taskString = tasks.joinToString(" ") { it.text }
        } else {
            buildToolName = "Maven"
        }

        val javaVersion = detectLanguageLevel(project, null)

        return BuildSystemContext(
            buildToolName = buildToolName,
            buildToolVersion = "",
            languageName = "Java",
            languageVersion = "$javaVersion",
            taskString = taskString
        )
    }

    private fun detectLanguageLevel(project: Project, sourceFile: PsiFile?): LanguageLevel? {
        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        if (projectSdk != null) {
            if (projectSdk.sdkType !is JavaSdkType) return null
            return PsiUtil.getLanguageLevel(project)
        }

        var moduleForFile = ModuleUtilCore.findModuleForFile(sourceFile)
        if (moduleForFile == null) {
            moduleForFile = ModuleManager.getInstance(project).modules.firstOrNull() ?: return null
        }

        val sdk = ModuleRootManager.getInstance(moduleForFile).sdk ?: return null
        if (sdk.sdkType !is JavaSdkType) return null

        return LanguageLevelUtil.getEffectiveLanguageLevel(moduleForFile)
    }

    companion object {

        val GRADLE_COMPLETION_COMPARATOR = Comparator<String> { o1, o2 ->
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
    }
}
