package com.phodal.shirecore.search.function

open class ScoredText(val text: String, val similarity: Double) {
    override fun toString(): String {
        return "Similarity: ${similarity}, Text: $text"
    }
}
