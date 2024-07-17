package com.phodal.shirecore.search.algorithm

open class JaccardSimilarity : Similarity {
    /**
     * The `tokenLevelJaccardSimilarity` method calculates the Jaccard similarity between a query string and an array of string
     * arrays (chunks). The Jaccard similarity is a measure of the similarity between two sets and is defined as the size of
     * the intersection divided by the size of the union of the two sets.
     *
     * @param query The query string to compare against the chunks.
     * @param chunks An array of string arrays (chunks) to compare against the query.
     * @return A two-dimensional array representing the Jaccard similarity scores between the query and each chunk.
     */
    override fun computeInputSimilarity(query: String, chunks: List<List<String>>): List<List<Double>> {
        val currentFileTokens = tokenize(query)
        return chunks.map { list ->
            list.map { it ->
                val tokenizedFile = tokenize(it)
                similarityScore(currentFileTokens, tokenizedFile)
            }
        }
    }

    fun similarityScore(set1: Set<String>, set2: Set<String>): Double {
        val intersectionSize = set1.intersect(set2).size
        val unionSize = set1.union(set2).size
        return intersectionSize.toDouble() / unionSize
    }

    /**
     * Calculates the similarity score between a given path and a set of strings.
     *
     * @param path The path to calculate similarity for.
     * @param sets The set of strings to compare with the path.
     * @return A number representing the similarity score between the path and the set of strings.
     */
    fun pathSimilarity(path: String, sets: Set<String>): Double {
        val splitPath = path.split('/')

        val set1 = splitPath.map(::tokenize)
            .reduce { acc, it -> acc.union(it) }

        val set2 = sets.map(::tokenize)
            .reduce { acc, it -> acc.union(it) }

        return similarityScore(set1, set2)
    }
}
