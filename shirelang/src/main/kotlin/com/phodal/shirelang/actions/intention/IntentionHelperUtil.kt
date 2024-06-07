package com.phodal.shirelang.actions.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirelang.actions.dynamic.DynamicShireActionService
import com.phodal.shirelang.compiler.frontmatter.HobbitHole

object IntentionHelperUtil {
    fun getAiAssistantIntentions(project: Project, editor: Editor, file: PsiFile): List<IntentionAction> {
        val shireActionConfigs = DynamicShireActionService.getInstance().getAction(ShireActionLocation.INTENTION_MENU)
        return shireActionConfigs.map { actionConfig ->
            ShireIntentionAction(actionConfig.name, actionConfig.config, file)
        }
    }
}

class ShireIntentionAction(name: String, val config: HobbitHole, file: PsiFile) : IntentionAction {
    override fun startInWriteAction(): Boolean = false

    override fun getFamilyName(): String = "Shire Intention"

    override fun getText(): String = "Shire Intention"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        return true
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        ShirelangNotifications.notify(project, "Shire Intention")
    }

}
