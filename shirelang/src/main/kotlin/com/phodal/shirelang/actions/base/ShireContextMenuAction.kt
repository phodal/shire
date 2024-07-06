package com.phodal.shirelang.actions.base

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.validator.WhenConditionValidator

class ShireContextMenuAction(private val config: DynamicShireActionConfig) :
    DumbAwareAction(config.name, config.hole?.description, ShireIcons.DEFAULT) {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val conditions = config.hole?.when_ ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return

        WhenConditionValidator.isAvailable(conditions, psiFile).let {
            e.presentation.isEnabled = it
            e.presentation.isVisible = it

            e.presentation.text = config.hole.description
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        ShireRunFileAction.executeShireFile(project, config, ShireRunFileAction.createRunConfig(e))
    }
}