package com.phodal.shire.inline

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.observable.util.*
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.RoundedLineBorder
import com.intellij.ui.components.JBTextArea
import com.phodal.shirecore.llm.LlmProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Rectangle2D
import java.util.concurrent.ConcurrentHashMap
import javax.swing.*
import javax.swing.border.Border

@Service(Service.Level.APP)
class ShireInlineChatService : Disposable {
    private val allChats: ConcurrentHashMap<String, ShireInlineChatPanel> = ConcurrentHashMap()

    fun showInlineChat(editor: Editor) {
        var canShowInlineChat = true
        if (allChats.containsKey(editor.virtualFile.url)) {
            val chatPanel: ShireInlineChatPanel = this.allChats[editor.virtualFile.url]!!
            canShowInlineChat = chatPanel.inlay?.offset != editor.caretModel.primaryCaret.offset
            closeInlineChat(editor)
        }

        if (canShowInlineChat) {
            if (editor.component is ShireInlineChatPanel) return

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
            allChats[editor.virtualFile.url] = panel
        }
    }

    override fun dispose() {
        allChats.values.forEach {
            closeInlineChat(it.editor)
        }

        allChats.clear()
    }

    fun closeInlineChat(editor: Editor) {
        val chatPanel = this.allChats[editor.virtualFile.url] ?: return

        chatPanel.inlay?.dispose()
        chatPanel.inlay = null

        // clear sessions for chat in here

        editor.contentComponent.remove(chatPanel)
        editor.contentComponent.revalidate()
        editor.contentComponent.repaint()
        allChats.remove(editor.virtualFile.url)
    }

    companion object {
        fun getInstance(): ShireInlineChatService {
            return ApplicationManager.getApplication().getService(ShireInlineChatService::class.java)
        }
    }
}

class ShireInlineChatPanel(val editor: Editor) : JPanel(GridBagLayout()), EditorCustomElementRenderer,
    Disposable {
    var inlay: Inlay<*>? = null
    val inputPanel = ShireInlineChatInputPanel(this, onSubmit = { input ->
        val flow: Flow<String>? = LlmProvider.provider(editor.project!!)?.stream(input, "", false)
        runBlocking {
            flow?.collect {
                println(it)
            }
        }
    })
    private var centerPanel: JPanel = JPanel(BorderLayout())
    private var container: Container? = null

    init {
        val createEmptyBorder = BorderFactory.createEmptyBorder(12, 12, 12, 12)
        val roundedLineBorder: Border = RoundedLineBorder(JBColor(Gray.xCD, Gray.x4D), 8, 1)
        border = BorderFactory.createCompoundBorder(createEmptyBorder, roundedLineBorder)

        isOpaque = false
        cursor = Cursor.getPredefinedCursor(0)
        background = JBColor(Gray.x99, Gray.x78)

        val c = GridBagConstraints()
        c.gridx = 0
        c.gridy = 0
        c.weightx = 1.0
        c.fill = 2
        add(inputPanel, c)

        val jPanel = JPanel(BorderLayout())
        jPanel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                IdeFocusManager.getInstance(editor.project).requestFocus(inputPanel.getInputComponent(), true)
            }
        })
        this.centerPanel = jPanel

        c.gridx = 0
        c.gridy = 1
        c.fill = 1
        add(this.centerPanel, c)

        isFocusCycleRoot = true
        focusTraversalPolicy = LayoutFocusTraversalPolicy()

        redraw()
    }

    override fun calcWidthInPixels(inlay: Inlay<*>): Int = size.width

    override fun calcHeightInPixels(inlay: Inlay<*>): Int = size.height

    private fun redraw() {
        ApplicationManager.getApplication().invokeLater {
            if (this.size.height != this.getMinimumSize().height) {
                this.size = Dimension(800, this.getMinimumSize().height)
                this.inlay?.update()

                this.revalidate()
                this.repaint()
            }
        }
    }

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

class ShireInlineChatInputPanel(
    val shireInlineChatPanel: ShireInlineChatPanel,
    val onSubmit: (String) -> Unit,
) : JPanel(GridBagLayout()) {
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

        val escapeAction = object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                ShireInlineChatService.getInstance().closeInlineChat(shireInlineChatPanel.editor)
            }
        }
        val enterAction = object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                textArea.text = ""
                onSubmit(textArea.text.trim())
            }
        }

        textArea.actionMap.put("escapeAction", escapeAction)
        textArea.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escapeAction")

        // submit with enter
        textArea.actionMap.put("enterAction", enterAction)
        textArea.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterAction")
        // newLine with shift + enter
        val insertBreakAction = object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                textArea.append("\n")
            }
        }
        textArea.actionMap.put("insert-break", insertBreakAction)
        textArea.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK), "insert-break")


        add(textArea)

        val document = textArea.document
        document.whenTextChanged {
            //
        }
    }

    fun getInputComponent(): Component = textArea
}