package com.phodal.shirelang.actions.context

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService
import com.phodal.shirelang.actions.validator.WhenConditionValidator

class ShireContextMenuActionGroup : ActionGroup() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isPopupGroup = DynamicShireActionService.getInstance().getAllActions().size > 1
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val configs: List<DynamicShireActionConfig> =
            DynamicShireActionService.getInstance().getAction(ShireActionLocation.CONTEXT_MENU)
        return configs.map { actionConfig ->
            ShireContextMenuAction(actionConfig)
        }.toTypedArray()
    }
}

class ShireContextMenuAction(private val config: DynamicShireActionConfig) :
    DumbAwareAction(config.name, config.hole?.description, ShireIcons.DEFAULT) {
    override fun update(e: AnActionEvent) {
        val conditions = config.hole?.when_ ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return

        WhenConditionValidator.isAvailable(conditions, psiFile)?.let {
            e.presentation.isEnabled = it
            e.presentation.isVisible = it
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        val file = e.getData(CommonDataKeys.PSI_FILE)

        config.hole?.setupProcessor(project, editor, file)
        config.hole?.pickupElement()

        ShireRunFileAction.executeShireFile(e, project, config)
    }
}
