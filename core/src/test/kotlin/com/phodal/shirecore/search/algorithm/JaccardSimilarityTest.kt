package com.phodal.shirecore.search.algorithm

import org.junit.Test
import junit.framework.TestCase.assertEquals

class JaccardSimilarityTest {
    @Test
    fun should_calculate_token_level_jaccard_similarity() {
        // given
        val jaccardSimilarity = JaccardSimilarity()
        val query = "test query"
        val chunks = listOf(listOf("test", "query"), listOf("another", "test"), listOf("query", "test"))

        // when
        val result = jaccardSimilarity.tokenLevelJaccardSimilarity(query, chunks)

        // then
        assertEquals(3, result.size)
        assertEquals(0.5, result[0][0])
        assertEquals(0.0, result[1][0])
        assertEquals(0.5, result[2][0])
    }

    @Test
    fun should_return_a_set_of_tokens() {
        // given
        val jaccardSimilarity = JaccardSimilarity()
        val input = "test input"

        // when
        val result = jaccardSimilarity.tokenize(input)

        // then
        assertEquals(2, result.size)
    }

    fun should_return_correct_similarity_score() {
        // given
        val jaccardSimilarity = JaccardSimilarity()
        val set1 = setOf("test", "query")
        val set2 = setOf("query", "another")

        // when
        val result = jaccardSimilarity.similarityScore(set1, set2)

        // then
        assertEquals(0.33, result, 0.01)
    }

    @Test
    fun should_return_the_correct_similarity_score() {
        // given
        val jaccardSimilarity = JaccardSimilarity()
        val path1 = "folder1/folder2/file1"
        val set2 = setOf("folder1", "folder2", "file2")
        val expectedScore: Double = 2.0 / 3.0

        // when
        val actualScore = jaccardSimilarity.pathSimilarity(path1, set2)

        // then
        assertEquals(expectedScore, actualScore)
    }
}
