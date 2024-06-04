package com.phodal.shire.action.context

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ShireContextActionGroup : ActionGroup() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
//        e.presentation.isPopupGroup = getContextProviders().size > 1
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return arrayOf()
    }
}
