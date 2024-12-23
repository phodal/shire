package com.phodal.shirelang.actions.vcs

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.variable.template.VariableActionEventDataHolder
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.base.DynamicShireActionService

class ShireVcsLogAction : AnAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.EDT

    private fun shireActionConfigs(project: Project) =
        DynamicShireActionService.getInstance(project).getActions(ShireActionLocation.VCS_LOG_MENU)

    override fun update(e: AnActionEvent) {
        val project = e.project ?: return
        val isOnlyOneConfig = shireActionConfigs(project).size == 1

        val hobbitHole = shireActionConfigs(project).firstOrNull()?.hole
        e.presentation.isVisible = isOnlyOneConfig
        e.presentation.isEnabled = hobbitHole != null && hobbitHole.enabled
        if (hobbitHole != null) {
            e.presentation.text = hobbitHole.name ?: "<Placeholder>"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        VariableActionEventDataHolder.putData(VariableActionEventDataHolder(e.dataContext))

        val config = shireActionConfigs(project).firstOrNull() ?: return
        ShireRunFileAction.executeFile(project, config, null)
    }
}
