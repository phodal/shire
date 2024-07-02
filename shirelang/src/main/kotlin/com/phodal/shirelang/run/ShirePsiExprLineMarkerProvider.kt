package com.phodal.shirelang.run

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.LEFT
import com.intellij.psi.PsiElement
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.psi.ShireFrontMatterEntry

class ShirePsiExprLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is ShireFrontMatterEntry) return null
        if (element.functionStatement == null) return null

        val key = element.frontMatterKey!!.firstChild

        val functionBody = element.functionStatement?.functionBody ?: return null
        when {
            functionBody.queryStatement != null -> {
                return LineMarkerInfo(key, key.textRange, ShireIcons.PsiExpr, null, null, LEFT)
                { "" }
            }

            functionBody.actionBody != null -> {
                return LineMarkerInfo(key, key.textRange, ShireIcons.Pipeline, null, null, LEFT)
                { "" }
            }
        }

        return null
    }
}
