package com.phodal.shirelang.actions.vcs

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.actions.base.ShireActionGroup

class ShireVcsActionGroup : ShireActionGroup() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isPopupGroup = getActionsByType(e, ShireActionLocation.COMMIT_MENU).size > 1
    }

    /// todo: spike for logic in commit menu
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return getActionsByType(e, ShireActionLocation.COMMIT_MENU)
    }
}
