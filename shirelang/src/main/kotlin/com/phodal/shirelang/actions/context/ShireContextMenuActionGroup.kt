package com.phodal.shirelang.actions.context

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirelang.actions.base.DynamicShireActionService

class ShireContextMenuActionGroup : ActionGroup() {
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isPopupGroup = DynamicShireActionService.getInstance().getAllActions().size > 1
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val actionService = DynamicShireActionService.getInstance()

        return actionService.getAction(ShireActionLocation.CONTEXT_MENU).map { actionConfig ->
            val menuAction = ShireContextMenuAction(actionConfig)
            if (actionConfig.hole?.shortcut != null) {
                actionService.bindShortcutToAction(menuAction, actionConfig.hole.shortcut)
            }

            menuAction
        }.toTypedArray()
    }
}

