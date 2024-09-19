package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.ide.DataManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.dsl.builder.*
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.markdown.CodeFence
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.middleware.PostProcessorContext
import com.phodal.shirecore.middleware.PostProcessorType
import com.phodal.shirecore.middleware.builtin.ui.WebViewWindow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.runBlocking
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class ShowWebviewProcessor : PostProcessor {
    override val processorName: String get() = PostProcessorType.ShowWebview.handleName
    private var continueMessage: String = ""
    private var webview: WebViewWindow? = null

    override fun isApplicable(context: PostProcessorContext): Boolean {
        return true
    }

    override fun execute(project: Project, context: PostProcessorContext, console: ConsoleView?, args: List<Any>): Any {
        var html: String? = (context.pipeData["output"])?.toString() ?: context.genText

        val dataContext = DataManager.getInstance().dataContextFromFocusAsync.blockingGet(10000)
            ?: throw IllegalStateException("No data context")

        class WebviewKeyAdapter : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    var result = ""
                    val stream = LlmProvider.provider(project)
                        ?.stream(
                            "according user input to modify code. Use input: $continueMessage\nCode: \n```html\n$html\n```",
                            "",
                            false
                        )

                    runBlocking {
                        stream?.cancellable()?.collect {
                            result += it
                        }
                    }

                    val newHtml = CodeFence.parse(result).text
                    logger<ShowWebviewProcessor>().info("Result: $result")
                    runInEdt {
                        webview?.loadHtml(newHtml ?: "")
                    }

                    html = newHtml
                    continueMessage = ""
                }
            }
        }

        runInEdt {
            webview = WebViewWindow()
            val component = webview!!.apply { loadHtml(html ?: "") }.component

            val panel = panel {
                row {
                    cell(component)
                }
                row {
                    textArea()
                        .align(Align.FILL)
                        .bindText(::continueMessage)
                        .applyToComponent {
                            addKeyListener(WebviewKeyAdapter())
                        }
                }
            }

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
