package com.phodal.shirecore.ui.input

import com.intellij.ide.ui.laf.darcula.DarculaUIUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.ui.ErrorBorderCapable
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.UIUtil
import java.awt.*
import java.awt.geom.Path2D
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import javax.swing.JComponent
import javax.swing.border.Border
import javax.swing.border.LineBorder

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

class ShireLineBorder(color: Color, thickness: Int, roundedCorners: Boolean, val radius: Int) :
    LineBorder(color, thickness, roundedCorners) {

    override fun paintBorder(component: Component?, graphics: Graphics?, x: Int, y: Int, width: Int, height: Int) {
        if (thickness > 0 && graphics is Graphics2D) {
            val oldColor = graphics.color
            graphics.color = lineColor
            val offs = thickness
            val size = offs + offs
            val outer: Shape
            val inner: Shape
            if (roundedCorners) {
                val arc: Float = (radius * 2).toFloat()
                outer = RoundRectangle2D.Float(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), arc, arc)
                inner = RoundRectangle2D.Float(
                    (x + offs).toFloat(), (y + offs).toFloat(), (width - size).toFloat(),
                    (height - size).toFloat(), arc, arc
                )
            } else {
                outer = Rectangle2D.Float(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
                inner = Rectangle2D.Float(
                    (x + offs).toFloat(),
                    (y + offs).toFloat(),
                    (width - size).toFloat(),
                    (height - size).toFloat()
                )
            }
            val shape = Path2D.Float(0)
            shape.append(outer, false)
            shape.append(inner, false)
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            graphics.fill(shape)
            graphics.color = oldColor
        }
    }
}