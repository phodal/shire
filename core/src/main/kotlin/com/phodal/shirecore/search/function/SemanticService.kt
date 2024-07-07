package com.phodal.shirecore.search.function

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.Service
import com.phodal.shirecore.search.embedding.InMemoryEmbeddingSearchIndex
import java.io.File

data class IndexEntry(
    var index: Int,
    var count: Int,
    var chunk: String,
    val embedding: FloatArray,
)

@Service(Service.Level.APP)
class SemanticService() {
    val index = InMemoryEmbeddingSearchIndex(
        File(PathManager.getSystemPath())
            .resolve("shire-semantic-search")
            .resolve("pattern-func").toPath()
    )

    private val embedding: LocalEmbedding = LocalEmbedding.create() ?: throw IllegalStateException("Can't create embedding")

    suspend fun embedText(chunk: String): FloatArray {
        return embedding.embed(chunk)
    }

    companion object {
        fun getInstance(): SemanticService =
            ApplicationManager.getApplication().getService(SemanticService::class.java)
    }

    fun chunking(path: String) {

    }
}