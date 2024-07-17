package com.phodal.shirecore.search.algorithm

import kotlin.math.log10

class BM25Similarity : Similarity {
    private val k1 = 1.5
    private val b = 0.75

    override fun computeInputSimilarity(query: String, chunks: List<List<String>>): List<List<Double>> {
        val docCount = chunks.size
        val avgDocLength = chunks.map { it.size }.average()
        val idfMap = computeIDF(chunks, docCount)

        // Tokenize the query
        val queryTerms = tokenize(query)

        return chunks.map { doc ->
            val docLength = doc.size
            queryTerms.map { term ->
                val tf = doc.count { it == term }.toDouble()
                val idf = idfMap[term] ?: 0.0
                val numerator = tf * (k1 + 1)
                val denominator = tf + k1 * (1 - b + b * (docLength / avgDocLength))
                idf * (numerator / denominator)
            }
        }
    }

    fun computeIDF(chunks: List<List<String>>, docCount: Int): Map<String, Double> {
        val termDocCount = mutableMapOf<String, Int>()

        chunks.forEach { doc ->
            doc.toSet().forEach { term ->
                termDocCount[term] = termDocCount.getOrDefault(term, 0) + 1
            }
        }

        return termDocCount.mapValues { (_, count) ->
            log10((docCount - count + 0.5) / (count + 0.5) + 1.0)
        }
    }
}

