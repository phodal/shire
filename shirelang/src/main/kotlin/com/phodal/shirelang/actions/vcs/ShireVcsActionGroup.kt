package com.phodal.shirelang.actions.vcs

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.variable.template.VariableActionEventDataHolder
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.base.DynamicShireActionConfig
import com.phodal.shirelang.actions.base.DynamicShireActionService

class ShireVcsActionGroup : ActionGroup() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val isMultipleActions = shireActionConfigs().size > 1
        e.presentation.isVisible = isMultipleActions
        e.presentation.isEnabled = shireActionConfigs().any { it.hole?.enabled == true }
        e.presentation.isPopupGroup = true
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return shireActionConfigs().map(::ShireVcsAction).toTypedArray()
    }

    private fun shireActionConfigs() =
        DynamicShireActionService.getInstance().getActions(ShireActionLocation.COMMIT_MENU)
}

class ShireVcsAction(val config: DynamicShireActionConfig) :
    DumbAwareAction(config.name, config.hole?.description, ShireIcons.DEFAULT) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        VariableActionEventDataHolder.putData(VariableActionEventDataHolder(e.dataContext))

        ShireRunFileAction.executeShireFile(project, config, null)
    }
}
