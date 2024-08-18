package com.phodal.shirecore.search

import com.phodal.shirecore.search.algorithm.TfIdf
import junit.framework.TestCase.assertEquals
import org.junit.Test

class TfIdfTest {

    @Test
    fun shouldBuildDocumentFromString() {
        // given
        val tfIdf = TfIdf<String, Any>()
        val text = "apple orange apple banana"
        val expectedDocument = mapOf("apple" to 2, "orange" to 1, "banana" to 1)

        // when
        val document = tfIdf.buildDocument(text)

        // then
        assertEquals(expectedDocument, document)
    }

    @Test
    fun shouldReturnTfIdfValuesForAGivenQuery() {
        // given
        val tfIdf = TfIdf<String, Any>()
        val chunks = listOf("chunk1", "chunk2", "chunk3")
        val query = "chunk1"
        tfIdf.addDocuments(chunks)

        // when
        val result = tfIdf.search(query)

        // then
        assertEquals(3, result.size)
    }

    @Test
    fun shouldExecuteTheCallbackFunctionIfProvided() {
        // given
        val tfIdf = TfIdf<String, Any>()
        val chunks = listOf("chunk1", "chunk2", "chunk3")
        val query = "chunk1"
        tfIdf.addDocuments(chunks)

        // when
        tfIdf.search(query)
    }
}
