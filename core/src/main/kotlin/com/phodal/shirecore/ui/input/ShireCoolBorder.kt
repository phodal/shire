package com.phodal.shirecore.ui.input

import com.intellij.ide.ui.laf.darcula.DarculaUIUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.ui.ErrorBorderCapable
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.UIUtil
import java.awt.Component
import java.awt.Graphics
import java.awt.Insets
import java.awt.Rectangle
import javax.swing.JComponent
import javax.swing.border.Border

class ShireCoolBorder(private val editor: EditorEx, parent: JComponent) : Border, ErrorBorderCapable {
    init {
        editor.addFocusListener(object : FocusChangeListener {
            override fun focusGained(editor2: Editor) {
                parent.repaint()
            }

            override fun focusLost(editor2: Editor) {
                parent.repaint()
            }
        })
    }

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        val r = Rectangle(x, y, width, height)
        JBInsets.removeFrom(r, JBInsets.create(1, 1))

        DarculaNewUIUtil.fillInsideComponentBorder(g, r, c.background)
        val enabled = c.isEnabled
        val hasFocus = UIUtil.isFocusAncestor(c)
        DarculaNewUIUtil.paintComponentBorder(g, r, DarculaUIUtil.getOutline(c as JComponent), hasFocus, enabled)
    }

    override fun getBorderInsets(c: Component): Insets = JBInsets.create(Insets(3, 8, 3, 3)).asUIResource()
    override fun isBorderOpaque(): Boolean = true
}