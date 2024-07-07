package com.phodal

import cc.unitmesh.cf.LocalEmbedding
import org.junit.Test

class EmbedTest {
    @Test
    fun shouldEmbeddingText() {
        val semantic = LocalEmbedding.create()
        val output = semantic.embed("item")
        println(output)
    }
}