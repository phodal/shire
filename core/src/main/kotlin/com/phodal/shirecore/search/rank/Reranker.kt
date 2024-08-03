package com.phodal.shirecore.search.rank

import com.intellij.openapi.project.Project
import com.phodal.shirecore.search.function.ScoredText

interface Reranker {
    val name: String
    suspend fun rerank(query: String, chunks: List<ScoredText>): List<ScoredText>

    companion object {
        fun create(name: String, project: Project): Reranker {
            return when (name) {
                "lostInTheMiddleRanker" -> LostInTheMiddleRanker()
                "llmReranker" -> LLMReranker(project)
                else -> throw IllegalArgumentException("Unknown reranker: $name")
            }
        }
    }
}