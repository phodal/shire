package com.phodal.shirecore.ui

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.util.Key
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.UIUtil
import javax.swing.JComponent

class ShireLanguageLabelAction: DumbAwareAction(), CustomComponentAction {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        val languageId = presentation.getClientProperty(SHIRE_LANGUAGE_LABEL_KEY) ?: ""
        val label = JBLabel(languageId)
        label.setOpaque(false)
        label.foreground = UIUtil.getLabelInfoForeground()
        return label
    }

    override fun updateCustomComponent(component: JComponent, presentation: Presentation) {
        if (component !is JBLabel) return

        val languageId = presentation.getClientProperty(SHIRE_LANGUAGE_LABEL_KEY) ?: ""
        if (languageId.isNotBlank() && component.text.isBlank()) {
            component.text = languageId
        }
    }

    override fun actionPerformed(e: AnActionEvent) {

    }

    override fun update(e: AnActionEvent) {
        val editor = e.dataContext.getData(CommonDataKeys.EDITOR) ?: return
        val lightVirtualFile = FileDocumentManager.getInstance().getFile(editor.document) as? LightVirtualFile ?: return
        e.presentation.putClientProperty(SHIRE_LANGUAGE_LABEL_KEY, lightVirtualFile.language.displayName)
    }

    companion object {
        val SHIRE_LANGUAGE_LABEL_KEY: Key<String> = Key.create("ShireLanguagePresentationKey")
    }
}
