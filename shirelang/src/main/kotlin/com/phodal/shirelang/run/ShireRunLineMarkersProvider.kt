package com.phodal.shirelang.run

import com.phodal.shirelang.actions.ShireRunFileAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.ShireLanguage
import com.phodal.shirelang.psi.ShireFile
import com.intellij.psi.PsiComment

class ShireRunLineMarkersProvider : RunLineMarkerContributor(), DumbAware {
    override fun getInfo(element: PsiElement): Info? {
        if (element.language !is ShireLanguage) return null
        val psiFile = element as? ShireFile ?: return null

        val actions = arrayOf<AnAction>(ActionManager.getInstance().getAction(ShireRunFileAction.ID))

        return Info(
            AllIcons.RunConfigurations.TestState.Run,
            { ShireBundle.message("shire.line.marker.run.0", psiFile.containingFile.name) },
            *actions
        )
    }

    private fun getCommentInfo(element: PsiElement): Info? {
        if (element !is PsiComment) return null
        val commentText = element.text
        if (!commentText.contains("```shire")) return null

        val actions = arrayOf<AnAction>(ActionManager.getInstance().getAction(ShireRunFileAction.ID))

        return Info(
            AllIcons.RunConfigurations.TestState.Run,
            { ShireBundle.message("shire.line.marker.run.comment") },
            *actions
        )
    }

    fun extractShireCodeFromComment(comment: String): String {
        val codeBlockStart = comment.indexOf("```shire")
        if (codeBlockStart == -1) return ""

        val codeBlockEnd = comment.indexOf("```", codeBlockStart + 7)
        if (codeBlockEnd == -1) return ""

        return comment.substring(codeBlockStart + 7, codeBlockEnd).trim()
    }
}
