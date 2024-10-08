package com.phodal.shirecore.agent.agenttool.browse

import com.phodal.shirecore.agent.agenttool.AgentToolContext
import com.phodal.shirecore.provider.agent.AgentTool
import com.phodal.shirecore.agent.agenttool.AgentToolResult
import com.phodal.shirecore.agent.agenttool.ua.RandomUserAgent
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class BrowseTool : AgentTool {
    override val name: String get() = "Browse"
    override val description: String = "Get the content of a given URL."

    override fun execute(context: AgentToolContext): AgentToolResult {
        return AgentToolResult(
            isSuccess = true,
            output = parse(context.argument).body
        )
    }

    companion object {
        /**
         * Doc for parseHtml
         *
         * Intellij API: [com.intellij.inspectopedia.extractor.utils.HtmlUtils.cleanupHtml]
         */
        fun parse(url: String): DocumentContent {
            val doc: Document = Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent(RandomUserAgent.random())
                .followRedirects(true)
                .get()

            return DocumentCleaner().cleanHtml(doc)
        }
    }
}

