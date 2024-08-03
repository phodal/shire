package com.phodal.shirecore.search.rank

import com.phodal.shirecore.search.function.IndexEntry

interface Reranker {
    val name: String
    suspend fun rerank(query: String, chunks: List<IndexEntry>): List<IndexEntry>
}
