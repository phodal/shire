package com.phodal.shirelang.compiler.hobbit.execute.processor

import com.phodal.shirecore.agent.agenttool.browse.BrowseTool
import com.phodal.shirelang.compiler.patternaction.PatternActionFuncDef
import com.phodal.shirelang.compiler.patternaction.PatternProcessor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

object CrawlProcessor: PatternProcessor {
    override val type: PatternActionFuncDef = PatternActionFuncDef.CRAWL

    suspend fun doExecute(url: String): String? {
        /// todo: parse github README.md if it's a github repo
        return BrowseTool.parse(url).body
    }

    fun execute(urls: Array<out String>): List<String> {
        val results = runBlocking {
            coroutineScope {
                urls.mapNotNull {
                    try {
                        doExecute(it)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }

        return results
    }
}
