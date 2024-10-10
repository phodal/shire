package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.ide.DataManager
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import java.util.concurrent.CompletableFuture
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.KeyStroke

object ApprovalExecuteProcessor {
    fun execute(
        myProject: Project,
        filename: Any,
        variableNames: Array<String>,
        variableTable: MutableMap<String, Any?>,
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
                    invokeLater {
                        val result = ExecuteProcessor.execute(myProject, filename, variableNames, variableTable)
                        future.complete(result)
                    }
                },
                {
                    popup.cancel()
                    future.complete("")
                })

            popup.showInBestPositionFor(dataContext)
        }

        return future.get()
    }
}

class PendingApprovalPanel : JPanel() {
    private val approveButton = JButton("Approve")

    //        .apply {
//        addActionListener { approve(it) }
//    }
    private val rejectButton = JButton("Reject")
//        .apply {
//        addActionListener { reject(it) }
//    }

    init {
        val layoutBuilder = panel {
            row {
                if (System.getProperty("os.name").contains("Mac")) {
                    label("⌘ + ↵ ")
                } else {
                    label("Ctrl + ↵ ")
                }
                cell(approveButton)

                if (System.getProperty("os.name").contains("Mac")) {
                    label("⌘ + ⌦")
                } else {
                    label("Ctrl + Del")
                }
                cell(rejectButton)
            }
        }

        layoutBuilder.border = JBUI.Borders.empty(0, 10)
        this.add(layoutBuilder)
    }

    fun setupKeyShortcuts(popup: JBPopup, approve: (Any) -> Unit, reject: (Any) -> Unit) {
        approveButton.addActionListener(approve)
        approveButton.registerKeyboardAction(
            {
                popup.closeOk(null)
                approveButton.doClick()
            }, KeyStroke.getKeyStroke("ctrl ENTER"), JComponent.WHEN_IN_FOCUSED_WINDOW
        )

        rejectButton.addActionListener(reject)
        rejectButton.registerKeyboardAction(
            {
                popup.closeOk(null)
                rejectButton.doClick()
            }, KeyStroke.getKeyStroke("ctrl DELETE"), JComponent.WHEN_IN_FOCUSED_WINDOW
        )
    }
}