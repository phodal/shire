// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.phodal.shirecore.search.embedding

import com.phodal.shirecore.search.function.ScoredText
import kotlin.collections.asSequence
import kotlin.collections.indices
import kotlin.sequences.filter
import kotlin.sequences.map
import kotlin.sequences.sortedByDescending
import kotlin.sequences.take
import kotlin.sequences.toList
import kotlin.to

interface EmbeddingSearchIndex {
    val size: Int
    var limit: Int?

    operator fun contains(id: String): Boolean
    fun clear()

    fun onIndexingStart()
    fun onIndexingFinish()

    suspend fun addEntries(values: Iterable<Pair<String, FloatArray>>, shouldCount: Boolean = false)

    suspend fun saveToDisk()
    suspend fun loadFromDisk()

    fun findClosest(searchEmbedding: FloatArray, topK: Int, similarityThreshold: Double? = null): List<ScoredText>
    fun streamFindClose(searchEmbedding: FloatArray, similarityThreshold: Double? = null): Sequence<ScoredText>

    fun estimateMemoryUsage(): Long
    fun estimateLimitByMemory(memory: Long): Int
    fun checkCanAddEntry(): Boolean
}

internal fun Map<String, FloatArray>.findClosest(
    searchEmbedding: FloatArray,
    topK: Int, similarityThreshold: Double?,
): List<ScoredText> {
    return asSequence()
        .map { it.key to searchEmbedding.times(it.value) }
        .filter { (_, similarity) -> if (similarityThreshold != null) similarity > similarityThreshold else true }
        .sortedByDescending { (_, similarity) -> similarity }
        .take(topK)
        .map { (id, similarity) -> ScoredText(id, similarity) }
        .toList()
}

internal fun Sequence<Pair<String, FloatArray>>.streamFindClose(
    queryEmbedding: FloatArray,
    similarityThreshold: Double?,
): Sequence<ScoredText> {
    return map { (id, embedding) -> id to queryEmbedding.times(embedding) }
        .filter { similarityThreshold == null || it.second > similarityThreshold }
        .map { (id, similarity) -> ScoredText(id, similarity) }
}

fun FloatArray.times(doubles: FloatArray): Double {
    require(size == doubles.size) { "Arrays must have the same size" }

    var result = 0.0
    for (i in indices) {
        result += this[i] * doubles[i]
    }
    return result
}
