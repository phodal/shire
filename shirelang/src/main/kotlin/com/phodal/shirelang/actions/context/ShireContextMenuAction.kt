package com.phodal.shirelang.actions.context

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAwareAction
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.base.DynamicShireActionConfig
import com.phodal.shirelang.actions.base.validator.WhenConditionValidator

class ShireContextMenuAction(private val config: DynamicShireActionConfig) :
    DumbAwareAction(config.name, config.hole?.description, ShireIcons.DEFAULT) {

    init {
        templatePresentation.text = config.name.ifBlank { "Unknown" }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }


    override fun update(e: AnActionEvent) {
        //2024-07-13 10:32:57,277 [51307999] SEVERE - #c.i.o.a.i.Utils - Empty menu item text for ShireContextMenuAction@EditorPopup (com.phodal.shirelang.actions.context.ShireContextMenuAction). The default action text must be specified in plugin.xml or its class constructor [Plugin: com.phodal.shire]
        // com.intellij.diagnostic.PluginException: Empty menu item text for ShireContextMenuAction@EditorPopup (com.phodal.shirelang.actions.context.ShireContextMenuAction). The default action text must be specified in plugin.xml or its class constructor [Plugin: com.phodal.shire]
        try {
            val conditions = config.hole?.when_ ?: return
            val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return

            WhenConditionValidator.isAvailable(conditions, psiFile).let {
                e.presentation.isEnabled = it
                e.presentation.isVisible = it

                e.presentation.text = config.hole.name
            }
        } catch (e: Exception) {
            logger<ShireContextMenuAction>().error("Error in ShireContextMenuAction", e)
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        ShireRunFileAction.executeShireFile(
            project,
            config,
            ShireRunFileAction.createRunConfig(e)
        )
    }
}