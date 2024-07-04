package com.phodal.shirelang.actions.context

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.actions.base.ShireContextMenuAction
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService

class ShireContextMenuActionGroup : ActionGroup() {
    override fun update(e: AnActionEvent) {
        e.presentation.isPopupGroup = DynamicShireActionService.getInstance().getAllActions().size > 1
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return getActionsByType(e, ShireActionLocation.CONTEXT_MENU)
    }

    private fun getActionsByType(e: AnActionEvent?, shireActionLocation: ShireActionLocation): Array<AnAction> {
        val actionService = DynamicShireActionService.getInstance()

        val configs: List<DynamicShireActionConfig> =
            actionService.getAction(shireActionLocation)
        return configs.map { actionConfig ->
            val menuAction = ShireContextMenuAction(actionConfig)
            if (actionConfig.hole?.shortcut != null) {
                actionService.bindShortcutToAction(menuAction, actionConfig.hole.shortcut)
            }
            menuAction
        }.toTypedArray()
    }
}

