package com.phodal.shirelang.actions.context

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.phodal.shirelang.actions.DynamicShireActionService

class ShireContextActionGroup : ActionGroup() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isPopupGroup = DynamicShireActionService.getInstance().getAllActions().size > 1
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
//        val configs = DynamicShireActionService.getInstance().getAllActions()
        return arrayOf()
    }
}
