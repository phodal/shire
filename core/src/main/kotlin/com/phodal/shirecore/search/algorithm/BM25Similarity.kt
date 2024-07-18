package com.phodal.shirecore.search.algorithm

import kotlin.math.log10

/**
 * BM25Similarity is a class that computes the similarity between a given query and a list of documents (chunks).
 * It uses the BM25 algorithm, which is a probabilistic information retrieval model.
 * The BM25 algorithm considers term frequency, inverse document frequency, and document length to compute relevance scores.
 *
 * @property k1 The term frequency saturation parameter.
 * @property b The document length normalization parameter.
 *
 * The class contains two main functions:
 * 1. computeInputSimilarity: Computes the BM25 similarity scores between the query and each document.
 * 2. computeIDF: Computes the inverse document frequency (IDF) for each term in the corpus.
 */
class BM25Similarity : Similarity {
    private val k1 = 1.5
    private val b = 0.75

    override fun computeInputSimilarity(query: String, chunks: List<List<String>>): List<List<Double>> {
        val docCount = chunks.size
        val avgDocLength = chunks.map { it.size }.average()
        val idfMap = computeIDF(chunks, docCount)

        // Tokenize the query
        val queryTerms = tokenize(query).groupBy { it }.mapValues { it.value.size }

        return chunks.map { doc ->
            val docLength = doc.size
            queryTerms.map { (term, queryTermFreq) ->
                val tf = doc.count { it == term }.toDouble()
                val idf = idfMap[term] ?: 0.0
                val numerator = tf * (k1 + 1)
                val denominator = tf + k1 * (1 - b + b * (docLength / avgDocLength))
                idf * (numerator / denominator) * queryTermFreq
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

