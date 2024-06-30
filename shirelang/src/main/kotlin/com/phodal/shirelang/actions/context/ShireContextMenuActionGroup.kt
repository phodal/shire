package com.phodal.shirelang.actions.context

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.actions.base.ShireActionGroup
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService

class ShireContextMenuActionGroup : ShireActionGroup() {
    override fun update(e: AnActionEvent) {
        e.presentation.isPopupGroup = DynamicShireActionService.getInstance().getAllActions().size > 1
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return getActionsByType(e, ShireActionLocation.CONTEXT_MENU)
    }
}

