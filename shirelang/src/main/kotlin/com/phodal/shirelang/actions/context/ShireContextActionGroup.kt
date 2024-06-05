package com.phodal.shirelang.actions.context

import com.intellij.icons.AllIcons
import com.intellij.idea.ActionsBundle
import com.intellij.internal.statistic.StatisticsBundle
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService

class ShireContextActionGroup : ActionGroup() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isPopupGroup = DynamicShireActionService.getInstance().getAllActions().size > 1
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val configs: List<DynamicShireActionConfig> = DynamicShireActionService.getInstance().getAllActions()
        return configs.map {
            DynamicShireAction(it)
        }.toTypedArray()
    }
}

class DynamicShireAction(it: DynamicShireActionConfig) :
    DumbAwareAction(it.name, it.config.description, ShireIcons.Idea) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        ShirelangNotifications.notify(project, "Click" + e.presentation)
    }
}
