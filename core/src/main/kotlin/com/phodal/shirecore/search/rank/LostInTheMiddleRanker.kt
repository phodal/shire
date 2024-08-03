package com.phodal.shirecore.search.rank

import com.phodal.shirecore.search.function.ScoredEntry

class LostInTheMiddleRanker() : Reranker {
    override val name = "lostInTheMiddleRanker"

    override suspend fun rerank(query: String, chunks: List<ScoredEntry>): List<ScoredEntry> {
        val sortedChunks = chunks.sortedBy { it.score }
        val result = mutableListOf<ScoredEntry>()

        for (i in sortedChunks.indices) {
            if (i % 2 == 0) {
                result.add(sortedChunks[i / 2])
            } else {
                result.add(sortedChunks[sortedChunks.size - i / 2 - 1])
            }
        }

        return result
    }
}