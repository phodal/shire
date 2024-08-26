package com.phodal.shirelang.compiler.hobbit.execute

import com.phodal.shirecore.agent.agenttool.browse.BrowseTool
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

object CrawlProcessor {
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
