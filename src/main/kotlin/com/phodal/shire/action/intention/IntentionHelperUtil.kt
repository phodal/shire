package com.phodal.shire.action.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

object IntentionHelperUtil {
    fun getAiAssistantIntentions(project: Project, editor: Editor, file: PsiFile): List<IntentionAction> {
        return emptyList()
    }
}