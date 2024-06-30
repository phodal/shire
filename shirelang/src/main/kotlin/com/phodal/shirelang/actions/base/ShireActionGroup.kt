package com.phodal.shirelang.actions.base

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService

abstract class ShireActionGroup : ActionGroup() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isPopupGroup = DynamicShireActionService.getInstance().getAllActions().size > 1
    }

    fun getActionsByType(e: AnActionEvent?, shireActionLocation: ShireActionLocation): Array<AnAction> {
        val configs: List<DynamicShireActionConfig> =
            DynamicShireActionService.getInstance().getAction(shireActionLocation)
        return configs.map { actionConfig ->
            ShireContextMenuAction(actionConfig)
        }.toTypedArray()
    }
}