package com.phodal.shire.openrewrite

import com.intellij.execution.ExecutionManager
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.shire.FileRunService

class OpenRewriteFileRunService : FileRunService {
    override fun isApplicable(project: Project, file: VirtualFile): Boolean {
        return isOpenWriteFile(project, file)
    }

    private fun isOpenWriteFile(
        project: Project,
        file: VirtualFile,
    ): Boolean {
        try {
            val clazz = Class.forName("com.intellij.openRewrite.OpenRewriteFileService")
            val getInstanceMethod = clazz.getDeclaredMethod("getInstance")
            val isRecipeMethod = clazz.getDeclaredMethod("isRecipe", PsiFile::class.java)

            val fileService = getInstanceMethod.invoke(null)
            val psiFile = runReadAction {
                PsiManager.getInstance(project).findFile(file)
            } ?: return false

            isRecipeMethod.isAccessible = true

            // 调用 isRecipe 方法
            val result = isRecipeMethod.invoke(fileService, psiFile) as Boolean
            return result
        } catch (e: Exception) {
            return false
        }
    }

    override fun runConfigurationClass(project: Project): Class<out RunProfile>? = null

    override fun runFile(project: Project, virtualFile: VirtualFile, psiElement: PsiElement?): String? {
        if (!isOpenWriteFile(project, virtualFile)) {
            return ""
        }

        val runManager = RunManager.getInstance(project)
        val allSettings = runManager.allSettings

        val settings = allSettings.firstOrNull { it ->
            val config = it.configuration
            val configClass = config::class.java

            if (configClass.name == "com.intellij.openRewrite.run.OpenRewriteRunConfiguration") {
//                val expandedWorkingDirectoryField = configClass.getDeclaredField("getExpandedWorkingDirectory()")
//                // method
//                val expandedWorkingDirectoryMethod = configClass.getDeclaredMethod("setExpandedWorkingDirectory")
//                expandedWorkingDirectoryMethod.isAccessible = true
//
//
//                expandedWorkingDirectory?.let {
//                    if (it == virtualFile.parent.path) {
//                        return@firstOrNull true
//                    }
//                }

                return@firstOrNull true
            } else {
                return@firstOrNull false
            }
        } ?: return ""
//            ?: run {
//            val newSettings = runManager.createConfiguration(
//                "", OpenRewriteRunConfigurationType.openRewriteRunConfigurationType().configurationFactories[0]
//            )
//            val config = newSettings.configuration as OpenRewriteRunConfiguration
//            config.apply {
//                activeRecipes = descriptor.name
//                generatedName = ""
//                workingDirectory = virtualFile.parent.path
//            }
//            runManager.setUniqueNameIfNeeded(newSettings)
//            runManager.setTemporaryConfiguration(newSettings)
//            newSettings
//        }

        if (settings == null) {
            throw RuntimeException("No OpenRewrite configuration found")
            return ""
        }

        val builder = ExecutionEnvironmentBuilder.createOrNull(DefaultRunExecutor.getRunExecutorInstance(), settings)
        builder?.let {
            ExecutionManager.getInstance(project).restartRunProfile(it.build())
        }

        return ""
    }
}