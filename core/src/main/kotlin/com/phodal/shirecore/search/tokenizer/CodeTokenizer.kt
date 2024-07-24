package com.phodal.shirecore.search.tokenizer

interface CodeTokenizer {
    fun tokenize(input: String): Set<String>
}