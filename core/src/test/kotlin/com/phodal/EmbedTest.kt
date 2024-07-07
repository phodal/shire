package com.phodal

import com.phodal.shirecore.search.function.LocalEmbedding
import kotlinx.coroutines.runBlocking
import org.junit.Test

class EmbedTest {
    @Test
    fun shouldEmbeddingText() {
        val semantic = LocalEmbedding.create(this.javaClass.classLoader)
        val output = runBlocking {
            semantic!!.embed("item")
        }

        println(output)
    }
}