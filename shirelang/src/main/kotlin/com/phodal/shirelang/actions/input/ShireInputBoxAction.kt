package com.phodal.shirelang.actions.input

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.psi.PsiElement
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.interaction.dto.CodeCompletionRequest
import com.phodal.shirecore.interaction.task.ChatCompletionTask
import com.phodal.shirecore.interaction.task.CodeCompletionTask
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService
import com.phodal.shirelang.actions.input.inlay.CustomInputBox
import com.phodal.shirelang.actions.input.inlay.CustomInputBox.Companion.CUSTOM_INPUT_CANCEL_ACTION
import com.phodal.shirelang.actions.input.inlay.CustomInputBox.Companion.CUSTOM_INPUT_SUBMIT_ACTION
import com.phodal.shirelang.actions.input.inlay.InlayPanel
import java.awt.event.ActionEvent
import javax.swing.AbstractAction

class ShireInputBoxAction : DumbAwareAction() {
    override fun getActionUpdateThread() = ActionUpdateThread.EDT

//    private fun shireActionConfigs() =
//        DynamicShireActionService.getInstance().getAction(ShireActionLocation.INPUT_BOX)
//
//    override fun update(e: AnActionEvent) {
//        e.presentation.isEnabled = shireActionConfigs().size == 1
//    }

    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val editor = dataContext.getData(CommonDataKeys.EDITOR) ?: return
        val offset = editor.caretModel.offset
        val project = dataContext.getData(CommonDataKeys.PROJECT) ?: return
        val element = e.getData(CommonDataKeys.PSI_ELEMENT)

        InlayPanel.add(editor as EditorEx, offset, CustomInputBox())?.let {
            doExecute(it, project, editor, element)
        }
    }

    private fun doExecute(
        inlay: InlayPanel<CustomInputBox>,
        project: Project,
        editor: EditorEx,
        element: PsiElement?,
    ) {
        val component = inlay.component

        val actionMap = component.actionMap

        actionMap.put(CUSTOM_INPUT_SUBMIT_ACTION, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                val text =
                    """Generate a concise code snippet with no extra text, description, or comments. 
                        |The code should achieve the following task: ${component.getText()}"""
                        .trimMargin()

                val offset = editor.caretModel.offset

                val request = runReadAction {
                    CodeCompletionRequest.create(editor, offset, element, null, userPrompt = text,
                        postExecute = { _, _ ->
                        })
                } ?: return

                val task = ChatCompletionTask(request)
                ProgressManager.getInstance()
                    .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))

                Disposer.dispose(inlay.inlay!!)
            }
        })

        actionMap.put(CUSTOM_INPUT_CANCEL_ACTION, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                Disposer.dispose(inlay.inlay!!)
            }
        })

        IdeFocusManager.getInstance(project).requestFocus(component, false)
        EditorUtil.disposeWithEditor(editor, inlay.inlay!!)
    }

}