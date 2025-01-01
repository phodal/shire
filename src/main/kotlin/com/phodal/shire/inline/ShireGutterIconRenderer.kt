package com.phodal.shire.inline

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.phodal.shire.ShireIdeaIcons
import javax.swing.Icon

class ShireGutterIconRenderer(
    val line: Int, val onClick: () -> Unit,
) : GutterIconRenderer() {
    override fun getClickAction(): AnAction {
        return object : AnAction() {
            override fun actionPerformed(e: AnActionEvent) {
                onClick()
            }
        }
    }

    override fun getIcon(): Icon = ShireIdeaIcons.Default
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShireGutterIconRenderer

        if (line != other.line) return false
        if (onClick != other.onClick) return false

        return true
    }

    override fun hashCode(): Int {
        var result = line
        result = 31 * result + onClick.hashCode()
        return result
    }

}