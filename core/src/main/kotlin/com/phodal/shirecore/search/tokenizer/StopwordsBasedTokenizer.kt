package com.phodal.shirecore.search.tokenizer

import com.phodal.shirecore.search.tokenizer.TermSplitter.splitTerms

val CHINESE_STOP_WORDS = listOf(
    "的", "地", "得", "和", "跟",
    "与", "及", "向", "并", "等",
    "更", "已", "含", "做", "我",
    "你", "他", "她", "们", "某",
    "该", "各", "每", "这", "那",
    "哪", "什", "么", "谁", "年",
    "月", "日", "时", "分", "秒",
    "几", "多", "来", "在", "就",
    "又", "很", "呢", "吧", "吗",
    "了", "嘛", "哇", "儿", "哼",
    "啊", "嗯", "是", "着", "都",
    "不", "说", "也", "看", "把",
    "还", "个", "有", "小", "到",
    "一", "为", "中", "于", "对",
    "会", "之", "第", "此", "或",
    "共", "按", "请"
)

class StopwordsBasedTokenizer private constructor() : CodeTokenizer {
    companion object {
        private var instance_: StopwordsBasedTokenizer? = null

        @JvmStatic
        fun instance(): StopwordsBasedTokenizer {
            if (instance_ == null) {
                instance_ = StopwordsBasedTokenizer()
            }
            return instance_!!
        }
    }

    private val stopWords = listOf("we", "our", "you", "it", "its", "they", "them", "their", "this", "that", "these", "those", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "can", "don", "t", "s", "will", "would", "should", "what", "which", "who", "when", "where", "why", "how", "a", "an", "the", "and", "or", "not", "no", "but", "because", "as", "until", "again", "further", "then", "once", "here", "there", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "above", "below", "to", "during", "before", "after", "of", "at", "by", "about", "between", "into", "through", "from", "up", "down", "in", "out", "on", "off", "over", "under", "only", "own", "same", "so", "than", "too", "very", "just", "now")

    private val programmingKeywords = listOf("if", "then", "else", "for", "while", "with", "def", "function", "return",
        "TODO", "import", "try", "catch", "raise", "finally", "repeat", "switch", "case", "match", "assert", "continue",
        "break", "const", "class", "enum", "struct", "static", "new", "super", "this", "var")

    private val javaKeywords = listOf("public", "private", "protected", "static", "final", "abstract", "interface", "implements", "extends", "throws", "throw", "try", "catch", "finally", "synchronized")

    private val chineseStopWords = CHINESE_STOP_WORDS

    private val stopWordsSet = setOf(
        *stopWords.toTypedArray(),
        *programmingKeywords.toTypedArray(),
        *javaKeywords.toTypedArray(),
        *chineseStopWords.toTypedArray()
    )

    override fun tokenize(input: String): Set<String> {
        return splitTerms(input).toList().filter { !stopWordsSet.contains(it) }.toSet()
    }
}
