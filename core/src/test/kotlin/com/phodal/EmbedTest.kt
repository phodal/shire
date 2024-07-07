package com.phodal

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.phodal.shirecore.search.function.LocalEmbedding
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test

class EmbedTest {
    @Test
    @Ignore
    fun shouldEmbeddingText() {
        val pluginClassLoader = EmbedTest::class.java.classLoader
        val tokenizerStream = pluginClassLoader.getResourceAsStream("model/tokenizer.json")
        val tokenizer = HuggingFaceTokenizer.newInstance(tokenizerStream, null)

        val ortEnv = OrtEnvironment.getEnvironment()
        val sessionOptions = OrtSession.SessionOptions()

        val onnxStream = pluginClassLoader.getResourceAsStream("model/model.onnx")!!
        // load onnxPath as byte[]
        val onnxPathAsByteArray = onnxStream.readAllBytes()
        val session = ortEnv.createSession(onnxPathAsByteArray, sessionOptions)

        val localEmbedding = LocalEmbedding(tokenizer, session, ortEnv)

        val output = runBlocking {
            localEmbedding.embed("item")
        }

        println(output)
    }
}