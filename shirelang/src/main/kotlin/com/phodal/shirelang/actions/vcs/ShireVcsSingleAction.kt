package com.phodal.shirelang.actions.vcs

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService

class ShireVcsSingleAction : DumbAwareAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.EDT

    private fun shireActionConfigs() =
        DynamicShireActionService.getInstance().getAction(ShireActionLocation.COMMIT_MENU)

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = shireActionConfigs().size == 1
        e.presentation.isEnabled = shireActionConfigs().size == 1
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val config = shireActionConfigs().first()
        ShireRunFileAction.executeShireFile(project, config, null)
    }
}