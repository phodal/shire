package com.phodal.shire.inline

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import java.awt.*
import java.awt.geom.Rectangle2D
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.LayoutFocusTraversalPolicy

@Service(Service.Level.APP)
class ShireInlineChatService : Disposable {
    fun showInlineChat(editor: Editor) {
        runInEdt {
            if (editor.component !is ShireInlineChatPanel) {
                val panel = ShireInlineChatPanel(editor)
                editor.contentComponent.add(panel)
                panel.setInlineContainer(editor.contentComponent)
                val offset = if (editor.selectionModel.hasSelection()) {
                    editor.selectionModel.selectionStart
                } else {
                    editor.caretModel.primaryCaret.offset
                }

                panel.createInlay(offset)
                IdeFocusManager.getInstance(editor.project).requestFocus(panel.inputPanel.getInputComponent(), true)
            }
        }
    }

    override fun dispose() {
        ///
    }

    companion object {
        fun getInstance(): ShireInlineChatService {
            return ApplicationManager.getApplication().getService(ShireInlineChatService::class.java)
        }
    }
}

class ShireInlineChatPanel(private val editor: Editor) : JPanel(GridBagLayout()), EditorCustomElementRenderer,
    Disposable {
    private var inlay: Inlay<*>? = null
    val inputPanel = ShireInlineChatInputPanel()
    private var centerPanel: JPanel = JPanel(BorderLayout())
    private var container: Container? = null

    init {
        border = JBUI.Borders.empty()
        isOpaque = false
        cursor = Cursor.getPredefinedCursor(0)

        val jPanel = JPanel(BorderLayout())
        jPanel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                IdeFocusManager.getInstance(editor.project).requestFocus(inputPanel.getInputComponent(), true)
            }
        })
        this.centerPanel = jPanel

        val c = GridBagConstraints()
        c.gridx = 0
        c.gridy = 1
        c.weightx = 1.0
        c.anchor = GridBagConstraints.NORTHWEST
        c.fill = GridBagConstraints.HORIZONTAL
        add(this.centerPanel, c)

        c.gridy = 0
        c.weighty = 0.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(inputPanel, c)

        isFocusCycleRoot = true
        focusTraversalPolicy = LayoutFocusTraversalPolicy()
    }

    override fun calcWidthInPixels(p0: Inlay<*>): Int = size.width

    override fun calcHeightInPixels(p0: Inlay<*>): Int = size.height

    fun createInlay(offset: Int) {
        inlay = editor.inlayModel.addBlockElement(offset, false, true, 1, this)
    }

    fun setInlineContainer(container: Container) {
        this.container = container
    }

    override fun paint(inlay: Inlay<*>, g: Graphics2D, targetRegion: Rectangle2D, textAttributes: TextAttributes) {
        bounds = inlay.bounds ?: return
        revalidate()
        repaint()
    }

    override fun dispose() {
        inlay?.dispose()
    }
}

class ShireInlineChatInputPanel : JPanel(GridBagLayout()) {
    private val textArea: JBTextArea

    init {
        layout = BorderLayout()
        textArea = JBTextArea().apply {
            isOpaque = false
            isFocusable = true
            lineWrap = true
            wrapStyleWord = true
            border = BorderFactory.createEmptyBorder(8, 5, 8, 5)
        }

        add(textArea)
    }

    fun getInputComponent(): JBTextArea {
        return textArea
    }
}