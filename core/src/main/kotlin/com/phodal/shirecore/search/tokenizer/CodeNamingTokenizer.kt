package com.phodal.shirecore.search.tokenizer

class CodeNamingTokenizer(opts: RegexTokenizerOptions? = null) : RegexpTokenizer(opts) {
    init {
        whitespacePattern = Regex(
            "(?<=[a-z])(?=[A-Z])|" +       // camelCase 分词
                    "(?<=[A-Z])(?=[A-Z][a-z])|" +  // PascalCase 分词
                    "(?<=[A-Za-z])(?=[0-9])|" +    // 字母和数字分词
                    "(?<=[0-9])(?=[A-Za-z])|" +    // 数字和字母分词
                    "_+"                           // under_score 分词
        )
    }

    override fun tokenize(s: String): List<String> {
        val results = whitespacePattern.split(s)
        return if (discardEmpty) {
            without(results, "", " ").map { it.lowercase() }
        } else {
            results.map { it.lowercase() }
        }
    }
}