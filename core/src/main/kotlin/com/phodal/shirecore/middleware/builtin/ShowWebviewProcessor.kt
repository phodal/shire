package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.ide.DataManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.middleware.PostProcessorContext
import com.phodal.shirecore.middleware.PostProcessorType

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
            val popupLocation = JBPopupFactory.getInstance().guessBestPopupLocation(dataContext)
            JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(html ?: "", MessageType.INFO, null)
                .setFadeoutTime(3000)
                .createBalloon()
                .show(popupLocation, Balloon.Position.above)
        }

        return ""
    }
}
