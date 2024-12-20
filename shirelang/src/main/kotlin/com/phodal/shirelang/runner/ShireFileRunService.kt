package com.phodal.shirelang.runner

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.RunProfile
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.phodal.shirecore.config.InteractionType
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.provider.shire.FileRunService
import com.phodal.shirelang.actions.ShireRunFileAction.Companion.executeShireFile
import com.phodal.shirelang.actions.base.DynamicShireActionConfig
import com.phodal.shirelang.compiler.ast.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.ShireConfiguration
import com.phodal.shirelang.run.ShireConfigurationType

class ShireFileRunService : FileRunService {
    override fun isApplicable(project: Project, file: VirtualFile): Boolean {
        return PsiManager.getInstance(project).findFile(file) is ShireFile
    }

    override fun runConfigurationClass(project: Project): Class<out RunProfile> = ShireConfiguration::class.java

    override fun createRunSettings(
        project: Project,
        virtualFile: VirtualFile,
        testElement: PsiElement?,
    ): RunnerAndConfigurationSettings? {
        val runManager = RunManager.getInstance(project)

        val psiFile = runReadAction {
            PsiManager.getInstance(project).findFile(virtualFile) as? ShireFile ?: return@runReadAction null
        } ?: return null

        val setting = runReadAction {
            runManager.createConfiguration(psiFile.name, ShireConfigurationType.getInstance())
        }

        val shireConfiguration = setting.configuration as ShireConfiguration
        shireConfiguration.name = virtualFile.nameWithoutExtension
        shireConfiguration.setScriptPath(virtualFile.path)

        setting.isTemporary = true

        runManager.setTemporaryConfiguration(setting)
        runManager.selectedConfiguration = setting

        return setting
    }

    override fun runFile(project: Project, virtualFile: VirtualFile, psiElement: PsiElement?): String? {
        val settings = createRunSettings(project, virtualFile, psiElement) ?: return null
        val psiFile = runReadAction {
            PsiManager.getInstance(project).findFile(virtualFile) as? ShireFile ?: return@runReadAction null
        } ?: return null

        var config = DynamicShireActionConfig.from(psiFile)
        if (config.hole == null) {
            config = config.copy(
                hole = HobbitHole.create(
                    "Chatbox",
                    "Run from Chatbox",
                    InteractionType.RightPanel,
                    ShireActionLocation.CHAT_BOX
                )
            )
        }

        /// we should recreate file in Here
        executeShireFile(project, config, settings)
        return "Running Shire file: ${virtualFile.name}"
    }
}