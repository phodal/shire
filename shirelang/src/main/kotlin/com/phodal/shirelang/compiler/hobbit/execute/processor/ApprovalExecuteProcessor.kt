package com.phodal.shirelang.compiler.hobbit.execute.processor

import com.intellij.ide.DataManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.phodal.shirelang.compiler.patternaction.PatternActionFuncDef
import com.phodal.shirelang.compiler.patternaction.PatternProcessor
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.util.concurrent.CompletableFuture
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.KeyStroke

object ApprovalExecuteProcessor: PatternProcessor {
    override val type: PatternActionFuncDef = PatternActionFuncDef.APPROVAL_EXECUTE

    fun execute(
        myProject: Project,
        filename: Any,
        variableNames: Array<String>,
        variableTable: MutableMap<String, Any?>,
        approve: ((Any) -> Unit)? = null,
        reject: (() -> Unit?)? = null
    ): Any {
        val dataContext = DataManager.getInstance().dataContextFromFocusAsync.blockingGet(10000)
            ?: throw IllegalStateException("No data context")

        val panel = PendingApprovalPanel()

        val future = CompletableFuture<Any>()

        runInEdt {
            val popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(panel, null)
                .setResizable(true)
                .setMovable(true)
                .setFocusable(true)
                .setRequestFocus(true)
                .setCancelOnClickOutside(false)
                .setCancelOnOtherWindowOpen(false)
                .setCancelOnWindowDeactivation(false)
                .setKeyboardActions(listOf())
                .createPopup()

            panel.setupKeyShortcuts(popup,
                {
                    popup.closeOk(null)
                    approve?.invoke("")
                    future.complete("")
                },
                {
                    popup.cancel()
                    reject?.invoke()
                    future.complete("")
                })

            popup.showInBestPositionFor(dataContext)
        }

        return future.get()
    }
}

class PendingApprovalPanel : JPanel() {
    private val approveButton = JButton("Approve")
    private val rejectButton = JButton("Reject")

    init {
        val layoutBuilder = panel {
            row {
                label(getShortcutLabel("⌘ + ↵", "Ctrl + ↵"))
                cell(approveButton)

                label(getShortcutLabel("⌘ + ⌦", "Ctrl + Del"))
                cell(rejectButton)
            }
        }


        layoutBuilder.border = JBUI.Borders.empty(0, 10)
        this.add(layoutBuilder)
    }

    private fun getShortcutLabel(shortcutForMac: String, shortcutForOthers: String): String {
        return if (System.getProperty("os.name").contains("Mac")) {
            shortcutForMac
        } else {
            shortcutForOthers
        }
    }

    fun setupKeyShortcuts(popup: JBPopup, approve: (Any) -> Unit, reject: (Any) -> Unit) {
        approveButton.addActionListener(approve)
        approveButton.registerKeyboardAction(
            {
                popup.closeOk(null)
                approveButton.doClick()
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW
        )

        rejectButton.addActionListener(reject)
        rejectButton.registerKeyboardAction(
            {
                popup.closeOk(null)
                rejectButton.doClick()
            }, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW
        )
    }
}