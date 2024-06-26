package com.phodal.shirelang.actions

import com.intellij.execution.ExecutionManager
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.phodal.shirelang.ShireActionStartupActivity
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.ShireConfiguration
import com.phodal.shirelang.run.ShireConfigurationType
import com.phodal.shirelang.run.ShireRunConfigurationProducer
import org.jetbrains.annotations.NonNls

class ShireRunFileAction : DumbAwareAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE) ?: return
        e.presentation.isEnabledAndVisible = file is ShireFile

        if (e.presentation.text.isNullOrBlank()) {
            e.presentation.text = "Run Shire file: ${file.name}"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE) as? ShireFile ?: return
        val project = file.project
        val config = DynamicShireActionConfig.from(file)

        val existingConfiguration = createRunConfig(e)
        executeShireFile(project, config, existingConfiguration)
    }

    companion object {
        const val ID: @NonNls String = "runShireFileAction"
        fun createRunConfig(e: AnActionEvent): RunnerAndConfigurationSettings? {
            val context = ConfigurationContext.getFromContext(e.dataContext, e.place)
            val configProducer = RunConfigurationProducer.getInstance(ShireRunConfigurationProducer::class.java)

            val existingConfiguration = configProducer.findExistingConfiguration(context)
            return existingConfiguration
        }

        fun executeShireFile(
            project: Project,
            config: DynamicShireActionConfig,
            runSettings: RunnerAndConfigurationSettings?,
        ) {
            val settings = runSettings ?: RunManager.getInstance(project)
                .createConfiguration(config.name, ShireConfigurationType::class.java)

            val runConfiguration = settings.configuration as ShireConfiguration

            runConfiguration.setScriptPath(config.shireFile.virtualFile.path)

            val executorInstance = DefaultRunExecutor.getRunExecutorInstance()
            val executionEnvironment = ExecutionEnvironmentBuilder
                .createOrNull(executorInstance, runConfiguration)
                ?.build() ?: return

            ExecutionManager.getInstance(project).restartRunProfile(executionEnvironment)
        }

        fun runFile(myProject: Project, fileName: String): Any {
            val file = runReadAction {
                ShireActionStartupActivity.obtainShireFiles(myProject).find {
                    it.name == fileName
                }
            } ?: return "File not found"

            ApplicationManager.getApplication().invokeLater({
                val config = DynamicShireActionConfig.from(file)
                executeShireFile(myProject, config, null)
            }, ModalityState.NON_MODAL)

            return ""
        }
    }
}
