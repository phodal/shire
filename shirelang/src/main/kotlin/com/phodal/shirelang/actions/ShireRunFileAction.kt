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
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.phodal.shirelang.ShireActionStartupActivity
import com.phodal.shirelang.actions.base.DynamicShireActionConfig
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc
import com.phodal.shirelang.compiler.patternaction.PatternActionTransform
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
            userInput: String? = null,
        ) {
            val settings = try {
                runSettings ?: RunManager.getInstance(project)
                    .createConfiguration(config.name, ShireConfigurationType::class.java)
            } catch (e: Exception) {
                logger<ShireRunFileAction>().error("Failed to create configuration", e)
                return
            }

            val runConfiguration = settings.configuration as ShireConfiguration

            runConfiguration.setScriptPath(config.shireFile.virtualFile.path)
            if (userInput != null) {
                runConfiguration.setUserInput(userInput)
            }

            val executorInstance = DefaultRunExecutor.getRunExecutorInstance()
            val executionEnvironment = ExecutionEnvironmentBuilder
                .createOrNull(executorInstance, runConfiguration)
                ?.build()

            if (executionEnvironment == null) {
                logger<ShireRunFileAction>().error("Failed to create execution environment")
                return
            }

            ExecutionManager.getInstance(project).restartRunProfile(executionEnvironment)
        }

        fun runFile(myProject: Project, fileName: String, variableTable: MutableMap<String, Any?>): Any {
            val lastOutput = variableTable["output"].toString()

            val file = runReadAction {
                ShireActionStartupActivity.obtainShireFiles(myProject).find {
                    it.name == fileName
                }
            } ?: return "File not found"

            ApplicationManager.getApplication().invokeLater({
                var config = DynamicShireActionConfig.from(file)
                if (lastOutput.isNotEmpty()) {
                    val hole = config.hole ?: HobbitHole(config.name)
                    hole.variables["output"] = PatternActionTransform(
                        variable = "output",
                        pattern = "",
                        patternActionFuncs = listOf(PatternActionFunc.Prompt(lastOutput))
                    )

                    config = config.copy(hole = hole)
                }
                executeShireFile(myProject, config, null)
            }, ModalityState.NON_MODAL)

            return ""
        }
    }
}
