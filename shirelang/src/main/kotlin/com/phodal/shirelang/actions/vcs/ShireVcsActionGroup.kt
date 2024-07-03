package com.phodal.shirelang.actions.vcs

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.base.ShireActionGroup
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService

class ShireVcsActionGroup : ShireActionGroup() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isPopupGroup = getActionsByType(e, ShireActionLocation.COMMIT_MENU).size > 1
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val configs: List<DynamicShireActionConfig> =
            DynamicShireActionService.getInstance().getAction(ShireActionLocation.COMMIT_MENU)
        return configs.map { actionConfig ->
            ShireVcsAction(actionConfig)
        }.toTypedArray()
    }
}

class ShireVcsAction(val config: DynamicShireActionConfig) :
    DumbAwareAction(config.name, config.hole?.description, ShireIcons.DEFAULT) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        ShireRunFileAction.executeShireFile(project, config, ShireRunFileAction.createRunConfig(e))
    }
}
