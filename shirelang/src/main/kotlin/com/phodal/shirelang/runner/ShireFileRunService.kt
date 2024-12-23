package com.phodal.shirelang.runner

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.RunProfile
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.phodal.shirecore.config.InteractionType
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.provider.shire.FileRunService
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.actions.ShireRunFileAction.Companion.executeFile
import com.phodal.shirelang.actions.base.DynamicShireActionConfig
import com.phodal.shirelang.compiler.ast.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.ShireConfiguration
import com.phodal.shirelang.run.ShireConfigurationType

class ShireFileRunService : FileRunService, Disposable {
    override fun isApplicable(project: Project, file: VirtualFile): Boolean {
        return file.extension == "shire"
    }

    override fun runConfigurationClass(project: Project): Class<out RunProfile> = ShireConfiguration::class.java

    override fun createRunSettings(
        project: Project,
        virtualFile: VirtualFile,
        testElement: PsiElement?,
    ): RunnerAndConfigurationSettings? {
        val runManager = RunManager.getInstance(project)

        val psiFile = ShireFile.lookup(project, virtualFile) ?: return null

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

    /**
     * Create ChatBox Run
     */
    override fun runFile(project: Project, virtualFile: VirtualFile, psiElement: PsiElement?): String? {
        val settings = createRunSettings(project, virtualFile, psiElement) ?: return null
        val psiFile = ShireFile.lookup(project, virtualFile) ?: return null

        var config = DynamicShireActionConfig.from(psiFile)
        if (config.hole == null) {
            config = config.copy(
                hole = HobbitHole.create(
                    ShireBundle.message("shire.actions.chat-box"),
                    ShireBundle.message("shire.actions.run-from-chat-box"),
                    InteractionType.RightPanel,
                    ShireActionLocation.CHAT_BOX
                )
            )
        }

        executeFile(project, config, settings)
        return "Running Shire file: ${virtualFile.name}"
    }

    override fun dispose() {

    }
}