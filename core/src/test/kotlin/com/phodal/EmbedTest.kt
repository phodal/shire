package com.phodal

import com.phodal.shirecore.search.function.LocalEmbedding
import org.junit.Test

class EmbedTest {
    @Test
    fun shouldEmbeddingText() {
        val semantic = LocalEmbedding.create()
        val output = semantic.embed("item")
        println(output)
    }
}