package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.ide.DataManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.dsl.builder.panel
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

        /// show a panel like webview?
        runInEdt {
            val panel = createPendingApprovalPanel()

            // show panel
            val popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(panel, null)
                .setResizable(true)
                .setMovable(true)
                .setTitle("Preview")
                .setFocusable(true)
                .setRequestFocus(true)
                .createPopup()

            popup.showInBestPositionFor(dataContext)
        }

        return ""
    }
}

fun createPendingApprovalPanel(): JPanel {
    val approveButton = JButton("Approve")
    val rejectButton = JButton("Reject")

    return panel {
        row {
            label("Pending Approval")
        }

        row {
            label("Use Command/Ctrl + Enter or Mouse to Approve")
        }

        row {
            approveButton.addActionListener {
                // Approve logic here
                println("Approved")
            }
            cell(approveButton)
        }

        row {
            label("Use Command/Ctrl + Delete or Mouse to Reject")
        }

        row {
            rejectButton.addActionListener {
                // Reject logic here
                println("Rejected")
            }
            cell(rejectButton)
        }

        // Adding keyboard shortcuts
        installShortcuts(approveButton, rejectButton)
    }
}

fun installShortcuts(approveButton: JButton, rejectButton: JButton) {
    val approveShortcut = KeymapManager.getInstance().activeKeymap.getShortcuts("Approve")
    approveButton.registerKeyboardAction(
        { approveButton.doClick() }, KeyStroke.getKeyStroke("ctrl ENTER"), JComponent.WHEN_IN_FOCUSED_WINDOW
    )

    val rejectShortcut = KeymapManager.getInstance().activeKeymap.getShortcuts("Reject")
    rejectButton.registerKeyboardAction(
        { rejectButton.doClick() }, KeyStroke.getKeyStroke("ctrl DELETE"), JComponent.WHEN_IN_FOCUSED_WINDOW
    )
}