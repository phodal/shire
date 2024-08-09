package com.phodal.shirelang.actions.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.actions.base.DynamicShireActionService
import com.phodal.shirelang.actions.base.validator.WhenConditionValidator
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import kotlin.collections.firstOrNull

class ShireIntentionAction(private val hobbitHole: HobbitHole?, val file: PsiFile, private val event: AnActionEvent?) :
    IntentionAction {
    override fun startInWriteAction(): Boolean = true
    override fun getFamilyName(): String = ShireBundle.message("shire.intention")
    override fun getText(): String = hobbitHole?.description ?: ShireBundle.message("shire.intention")

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        val conditions = hobbitHole?.when_ ?: return true
        return WhenConditionValidator.isAvailable(conditions, file)
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val config = DynamicShireActionService.getInstance()
            .getAction(ShireActionLocation.INTENTION_MENU)
            .firstOrNull { it.hole == hobbitHole } ?: return

        ShireRunFileAction.executeShireFile(project, config, null)
    }

}