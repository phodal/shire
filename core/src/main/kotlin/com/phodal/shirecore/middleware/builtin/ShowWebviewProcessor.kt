package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.ide.DataManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.middleware.PostProcessorContext
import com.phodal.shirecore.middleware.PostProcessorType
import com.phodal.shirecore.middleware.builtin.ui.WebViewWindow
import java.awt.Dimension

class ShowWebviewProcessor : PostProcessor {
    override val processorName: String get() = PostProcessorType.ShowWebview.handleName

    override fun isApplicable(context: PostProcessorContext): Boolean {
        return true
    }

    override fun execute(project: Project, context: PostProcessorContext, console: ConsoleView?, args: List<Any>): Any {
        val html: String? = (context.pipeData["output"])?.toString() ?: context.genText

        val dataContext = DataManager.getInstance().dataContextFromFocusAsync.blockingGet(10000)
            ?: throw IllegalStateException("No data context")

        runInEdt {
            val component = WebViewWindow().apply { loadHtml(html ?: "") }.component

            val popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(component, null)
                .setMinSize(Dimension(JBUI.scale(640), JBUI.scale(320)))
                .setRequestFocus(true)
                .createPopup()

            popup.showInBestPositionFor(dataContext)
        }

        return ""
    }
}
