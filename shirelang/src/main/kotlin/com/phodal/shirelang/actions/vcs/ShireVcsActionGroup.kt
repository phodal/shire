package com.phodal.shirelang.actions.vcs

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService

class ShireVcsActionGroup : ActionGroup() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
//        e.presentation.isPopupGroup = shireActionConfigs().size > 1
        e.presentation.isPopupGroup = true
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return shireActionConfigs().map(::ShireVcsAction).toTypedArray()
    }

    private fun shireActionConfigs() =
        DynamicShireActionService.getInstance().getAction(ShireActionLocation.COMMIT_MENU)
}

class ShireVcsAction(val config: DynamicShireActionConfig) :
    DumbAwareAction(config.name, config.hole?.description, ShireIcons.DEFAULT) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        ShireRunFileAction.executeShireFile(project, config, null)
    }
}
