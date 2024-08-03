package com.phodal.shirecore.search.rank

import cc.unitmesh.nlp.embedding.Embedding
import com.phodal.shirecore.search.function.IndexEntry

interface Reranker {
    val name: String
    suspend fun rerank(query: String, chunks: List<IndexEntry>): Embedding
}
