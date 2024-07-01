package com.phodal.shirelang.run

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.psi.ShireQueryStatement

class ShirePsiExprLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is ShireQueryStatement) return null

        return LineMarkerInfo(
            element,
            element.textRange,
            ShireIcons.PsiExpr,
            null,
            null,
            GutterIconRenderer.Alignment.LEFT
        ) {
            ""
        }
    }
}
