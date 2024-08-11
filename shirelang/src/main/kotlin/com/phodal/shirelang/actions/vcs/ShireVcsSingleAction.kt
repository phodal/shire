package com.phodal.shirelang.actions.vcs

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.template.VariableActionEventDataHolder
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.base.DynamicShireActionService

class ShireVcsSingleAction : DumbAwareAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.EDT

    private fun shireActionConfigs() =
        DynamicShireActionService.getInstance().getAction(ShireActionLocation.COMMIT_MENU)

    override fun update(e: AnActionEvent) {
        val isOnlyOneConfig = shireActionConfigs().size == 1
        e.presentation.isVisible = isOnlyOneConfig
        e.presentation.isEnabled = isOnlyOneConfig

        e.presentation.text = shireActionConfigs().firstOrNull()?.hole?.name ?: "<Placeholder>"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        VariableActionEventDataHolder.putData(VariableActionEventDataHolder(e.dataContext))

        val config = shireActionConfigs().first()
        ShireRunFileAction.executeShireFile(project, config, null)
    }
}