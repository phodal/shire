package com.phodal.shirelang.actions.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiFile
import javax.swing.Icon
import com.phodal.shire.ShireMainBundle
import com.phodal.shire.ShireIdeaIcons
import com.phodal.shirelang.actions.intention.ui.CustomPopupStep

class ShireIntentionHelper : IntentionAction, Iconable {
    override fun startInWriteAction(): Boolean = false
    override fun getText(): String = ShireMainBundle.message("intentions.assistant.name")
    override fun getFamilyName(): String = ShireMainBundle.message("intentions.assistant.name")
    override fun getIcon(flags: Int): Icon = ShireIdeaIcons.DEFAULT
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (file == null) return false

        val instance = InjectedLanguageManager.getInstance(project)
        if (instance.getTopLevelFile(file)?.virtualFile == null) {
            return false
        }

        val intentions = IntentionHelperUtil.getAiAssistantIntentions(project, editor, file, null)
        return intentions.isNotEmpty()
    }

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val intentions = IntentionHelperUtil.getAiAssistantIntentions(project, editor, file, null)

        val title = ShireMainBundle.message("intentions.assistant.popup.title")
        val popupStep = CustomPopupStep(intentions, project, editor, file, title)
        val popup = JBPopupFactory.getInstance().createListPopup(popupStep)

        popup.showInBestPositionFor(editor)
    }

}
