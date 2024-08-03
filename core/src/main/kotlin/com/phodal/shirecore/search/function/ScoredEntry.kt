package com.phodal.shirecore.search.function

import com.intellij.openapi.vfs.VirtualFile

data class ScoredEntry(
    var index: Int,
    var count: Int,
    var chunk: String,
    val file: VirtualFile? = null,
    var embedding: FloatArray? = null,
    val score: Double = 0.0
): ScoredText(chunk, score) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScoredEntry

        if (index != other.index) return false
        if (count != other.count) return false
        if (chunk != other.chunk) return false
        if (file != other.file) return false
        if (embedding != null) {
            if (other.embedding == null) return false
            if (!embedding.contentEquals(other.embedding)) return false
        } else if (other.embedding != null) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + count
        result = 31 * result + chunk.hashCode()
        result = 31 * result + (file?.hashCode() ?: 0)
        result = 31 * result + (embedding?.contentHashCode() ?: 0)
        result = 31 * result + score.hashCode()
        return result
    }
}