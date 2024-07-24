package com.phodal.shirecore.search.algorithm

import com.phodal.shirecore.search.tokenizer.StopwordsBasedTokenizer

interface Similarity {
    fun tokenize(input: String): Set<String> {
        return StopwordsBasedTokenizer.instance().tokenize(input).toSet()
    }

    fun computeInputSimilarity(query: String, chunks: List<List<String>>): List<List<Double>>
}
