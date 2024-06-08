package com.phodal.shirelang.actions.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService
import com.phodal.shirelang.compiler.hobbit.HobbitHole

object IntentionHelperUtil {
    fun getAiAssistantIntentions(project: Project, editor: Editor?, file: PsiFile): List<IntentionAction> {
        val shireActionConfigs = DynamicShireActionService.getInstance().getAction(ShireActionLocation.INTENTION_MENU)
        return shireActionConfigs.map { actionConfig ->
            ShireIntentionAction(actionConfig.name, actionConfig.hole, file)
        }
    }
}

class ShireIntentionAction(name: String, private val hobbitHole: HobbitHole?, file: PsiFile) : IntentionAction {
    override fun startInWriteAction(): Boolean = false

    override fun getFamilyName(): String = "Shire Intention"

    override fun getText(): String = "Shire Intention"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        return true
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        hobbitHole?.setupProcessor(project, editor, file)
        hobbitHole?.pickupElement()
        ShirelangNotifications.notify(project, "Shire Intention")
    }

}
