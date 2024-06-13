package com.phodal.shirelang.actions.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService
import com.phodal.shirelang.actions.validator.WhenConditionValidator
import com.phodal.shirelang.compiler.hobbit.HobbitHole

object IntentionHelperUtil {
    fun getAiAssistantIntentions(project: Project, editor: Editor?, file: PsiFile): List<IntentionAction> {
        val shireActionConfigs = DynamicShireActionService.getInstance().getAction(ShireActionLocation.INTENTION_MENU)
        return shireActionConfigs.map { actionConfig ->
            ShireIntentionAction(actionConfig.hole, file)
        }
    }
}

class ShireIntentionAction(private val hobbitHole: HobbitHole?, file: PsiFile) : IntentionAction {
    override fun startInWriteAction(): Boolean = false
    override fun getFamilyName(): String = ShireBundle.message("shire.intention")
    override fun getText(): String = ShireBundle.message("shire.intention")

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        val conditions = hobbitHole?.when_ ?: return true
        return WhenConditionValidator.isAvailable(conditions, file)
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        hobbitHole?.setupProcessor(project, editor, file)
        hobbitHole?.pickupElement()
        ShirelangNotifications.notify(project, "Shire Intention")
    }

}
