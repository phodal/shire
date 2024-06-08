package com.phodal.shirelang.java.util

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
import com.phodal.shirelang.java.impl.GRADLE_COMPLETION_COMPARATOR
import org.jetbrains.plugins.gradle.service.project.GradleTasksIndices
import org.jetbrains.plugins.gradle.util.GradleConstants

object JavaToolchain {
    fun collectGradleTasksWithCheck(project: Project): List<TextCompletionInfo> {
        val projectDataManager = ProjectDataManager.getInstance()
        val projectsData = projectDataManager.getExternalProjectsData(project, GradleConstants.SYSTEM_ID)

        return if (projectsData.isNotEmpty()) {
            collectGradleTasks(project)
        } else {
            emptyList()
        }
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

    fun detectLanguageLevel(project: Project, sourceFile: PsiFile?): LanguageLevel? {
        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        if (projectSdk != null) {
            if (projectSdk.sdkType !is JavaSdkType) return null
            return PsiUtil.getLanguageLevel(project)
        }

        val moduleForFile = ModuleUtilCore.findModuleForFile(sourceFile)
            ?: ModuleManager.getInstance(project).modules.firstOrNull()
            ?: return null

        if (ModuleRootManager.getInstance(moduleForFile).sdk !is JavaSdkType) return null

        return LanguageLevelUtil.getEffectiveLanguageLevel(moduleForFile)
    }

}