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
        val project = e.project ?: return
        e.presentation.isPopupGroup = DynamicShireActionService.getInstance(project).getAllActions().size > 1
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val project = e?.project ?: return emptyArray()
        val actionService = DynamicShireActionService.getInstance(project)

        return actionService.getActions(ShireActionLocation.CONTEXT_MENU).mapNotNull { actionConfig ->
            if (actionConfig.hole == null) return@mapNotNull null
            if (!actionConfig.hole.enabled) return@mapNotNull null

            val menuAction = ShireContextMenuAction(actionConfig)
            if (actionConfig.hole.shortcut != null) {
                actionService.bindShortcutToAction(menuAction, actionConfig.hole.shortcut)
            }

            menuAction
        }.toTypedArray()
    }
}

