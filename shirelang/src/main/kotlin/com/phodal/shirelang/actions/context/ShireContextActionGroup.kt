package com.phodal.shirelang.actions.context

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService

class ShireContextActionGroup : ActionGroup() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isPopupGroup = DynamicShireActionService.getInstance().getAllActions().size > 1
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val configs: List<DynamicShireActionConfig> =
            DynamicShireActionService.getInstance().getAction(ShireActionLocation.CONTEXT_MENU)
        return configs.map { actionConfig ->
            DynamicShireAction(actionConfig)
        }.toTypedArray()
    }
}

class DynamicShireAction(private val config: DynamicShireActionConfig) :
    DumbAwareAction(config.name, config.hole?.description, ShireIcons.DEFAULT) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR)
        val file = e.getData(CommonDataKeys.PSI_FILE)

        config.hole?.setupProcessor(project, editor, file)
        config.hole?.pickupElement()

        ShireRunFileAction.executeShireFile(e, project, config)
    }
}
