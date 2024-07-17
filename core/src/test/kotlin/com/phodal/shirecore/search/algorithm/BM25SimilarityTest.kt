package com.phodal.shirecore.search.algorithm

import junit.framework.TestCase.*
import org.junit.Test
import kotlin.math.log10

/**
 * Unit tests for BM25Similarity class.
 */
class BM25SimilarityTest {
    @Test
    fun testComputeInputSimilarity() {
        val bm25 = BM25Similarity()

        val query = "sample query"
        val chunks = listOf(
            listOf("this", "is", "a", "sample", "document"),
            listOf("this", "document", "is", "another", "sample"),
            listOf("one", "more", "sample", "document")
        )

        val similarity = bm25.computeInputSimilarity(query, chunks)

        assertNotNull(similarity)
        assertEquals(3, similarity.size) // We have 3 documents
        similarity.forEach { docSim ->
            assertEquals(2, docSim.size) // Our query has 2 terms
        }

        // Print similarity for manual inspection (not a part of actual tests)
        similarity.forEachIndexed { index, docSim ->
            println("Document $index: $docSim")
        }
    }

    @Test
    fun testComputeIDF() {
        val bm25 = BM25Similarity()

        val chunks = listOf(
            listOf("this", "is", "a", "sample", "document"),
            listOf("this", "document", "is", "another", "sample"),
            listOf("one", "more", "sample", "document")
        )
        val docCount = chunks.size

        val idfMap = bm25.computeIDF(chunks, docCount)

        assertNotNull(idfMap)
        assertTrue(idfMap.isNotEmpty())
        assertEquals(8, idfMap.size) // There are 8 unique terms

        // Validate some IDF values manually
        val expectedIDFThis = log10((docCount - 2 + 0.5) / (2 + 0.5) + 1.0)
        val expectedIDFSample = log10((docCount - 3 + 0.5) / (3 + 0.5) + 1.0)
        val expectedIDFOne = log10((docCount - 1 + 0.5) / (1 + 0.5) + 1.0)

        assertEquals(expectedIDFThis, idfMap["this"])
        assertEquals(expectedIDFSample, idfMap["sample"])
        assertEquals(expectedIDFOne, idfMap["one"])

        // Print IDF map for manual inspection (not a part of actual tests)
        idfMap.forEach { (term, idf) ->
            println("Term: $term, IDF: $idf")
        }
    }

    @Test
    fun `should compute similarity for query and documents correctly`() {
        val similarity = BM25Similarity()
        val chunks = listOf(
            listOf("apple", "banana", "apple"),
            listOf("banana", "cherry"),
            listOf("apple", "cherry")
        )
        val query = "apple banana"

        val result = similarity.computeInputSimilarity(query, chunks)

        assertNotNull(result)
        assertEquals(3, result.size) // Ensure one result per document
        assertEquals(2, result[0].size) // Ensure one score per term in the query

        // Check if the computed similarity values are non-negative
        val allPositive = result.flatten().all { it >= 0.0 }
        assertTrue(allPositive)
    }

    @Test
    fun `should handle query term not present in documents`() {
        val similarity = BM25Similarity()
        val chunks = listOf(
            listOf("apple", "banana", "apple"),
            listOf("banana", "cherry"),
            listOf("apple", "cherry")
        )
        val query = "apple orange"

        val result = similarity.computeInputSimilarity(query, chunks)

        assertNotNull(result)
        assertEquals(3, result.size) // Ensure one result per document
        assertEquals(2, result[0].size) // Ensure one score per term in the query

        // Check if the score for the term 'orange' is 0.0 as it is not present in the documents
        assertEquals(0.0, result[0][1])
    }
}
