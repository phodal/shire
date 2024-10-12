package com.phodal.shirelang.highlight

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.phodal.shirelang.psi.ShireTypes

class ShireHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (PsiUtilCore.getElementType(element)) {
            ShireTypes.IDENTIFIER -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .textAttributes(DefaultLanguageHighlighterColors.IDENTIFIER)
                    .create()
            }
        }
    }
}
