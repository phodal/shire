package com.phodal.shirecore.search.function

import cc.unitmesh.cf.Embedding
import cc.unitmesh.cf.LocalEmbedding
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.Service
import com.phodal.shirecore.search.embedding.InMemoryEmbeddingSearchIndex
import java.io.File

data class IndexEntry(
    var index: Int,
    var count: Int,
    val embedding: Embedding,
)

@Service(Service.Level.APP)
class EmbeddingService() {
    val index = InMemoryEmbeddingSearchIndex(
        File(PathManager.getSystemPath())
            .resolve("shire-semantic-search")
            .resolve("pattern-func").toPath()
    )

    private val embedding: LocalEmbedding = LocalEmbedding.create()

    fun embedText(chunk: String): List<Double> {
        return embedding.embed(chunk)
    }

    companion object {
        fun getInstance(): EmbeddingService =
            ApplicationManager.getApplication().getService(EmbeddingService::class.java)
    }
}