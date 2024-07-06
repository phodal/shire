package com.phodal.shirelang.actions.input

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.IdeFocusManager
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirelang.actions.ShireRunFileAction.Companion.executeShireFile
import com.phodal.shirelang.actions.dynamic.DynamicShireActionConfig
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService
import com.phodal.shirelang.actions.input.inlay.CustomInputBox
import com.phodal.shirelang.actions.input.inlay.CustomInputBox.Companion.CUSTOM_INPUT_CANCEL_ACTION
import com.phodal.shirelang.actions.input.inlay.CustomInputBox.Companion.CUSTOM_INPUT_SUBMIT_ACTION
import com.phodal.shirelang.actions.input.inlay.InlayPanel
import java.awt.event.ActionEvent
import javax.swing.AbstractAction

class ShireInputBoxAction : DumbAwareAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.EDT

    private fun shireActionConfigs() =
        DynamicShireActionService.getInstance().getAction(ShireActionLocation.INPUT_BOX)

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = shireActionConfigs().isNotEmpty()

        e.presentation.text = shireActionConfigs().first().hole?.description ?: ""
    }

    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val editor = dataContext.getData(CommonDataKeys.EDITOR) ?: return
        val offset = editor.caretModel.offset
        val project = dataContext.getData(CommonDataKeys.PROJECT) ?: return

        val instance = DynamicShireActionService.getInstance()
        val config = shireActionConfigs().first()
        if (config.hole?.shortcut != null) {
            instance.bindShortcutToAction(this, config.hole.shortcut)
        }

        InlayPanel.add(editor as EditorEx, offset, CustomInputBox())?.let {
            doExecute(it, project, editor, config)
        }
    }

    private fun doExecute(
        inlay: InlayPanel<CustomInputBox>,
        project: Project,
        editor: EditorEx,
        config: DynamicShireActionConfig,
    ) {
        val component = inlay.component
        component.actionMap.put(CUSTOM_INPUT_SUBMIT_ACTION, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                val inputText = component.getText()
                executeShireFile(project, config, null, userInput = inputText)
                Disposer.dispose(inlay.inlay!!)
            }
        })

        component.actionMap.put(CUSTOM_INPUT_CANCEL_ACTION, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                Disposer.dispose(inlay.inlay!!)
            }
        })

        IdeFocusManager.getInstance(project).requestFocus(component, false)
        EditorUtil.disposeWithEditor(editor, inlay.inlay!!)
    }

}