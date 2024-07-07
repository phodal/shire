package com.phodal.shirecore.search.function

import cc.unitmesh.cf.LocalEmbedding
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service

@Service(Service.Level.APP)
class EmbeddingFunc {
    private val embedding: LocalEmbedding = LocalEmbedding.create()

    fun embed(text: String) : List<Double> {
        return embedding.embed(text)
    }

    companion object {
        fun getInstance(): EmbeddingFunc =
            ApplicationManager.getApplication().getService(EmbeddingFunc::class.java)
    }
}