package com.phodal.shirelang.actions.context

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService
import com.phodal.shirelang.compiler.ShireCompiler

class ShireContextActionGroup : ActionGroup() {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isPopupGroup = DynamicShireActionService.getInstance().getAllActions().size > 1
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val configs: List<DynamicShireActionConfig> = DynamicShireActionService.getInstance().getAllActions()
        return configs.map { actionConfig ->
            DynamicShireAction(actionConfig)
        }.toTypedArray()
    }
}

class DynamicShireAction(private val actionConfig: DynamicShireActionConfig) :
    DumbAwareAction(actionConfig.name, actionConfig.config.description, ShireIcons.Idea) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // todo: handle editor and element
        val editor = e.getData(CommonDataKeys.EDITOR) as EditorEx? ?: return
        val element = e.getData(CommonDataKeys.PSI_ELEMENT)

        ShireRunFileAction.executeShireFile(e, project, actionConfig.file)
    }
}
