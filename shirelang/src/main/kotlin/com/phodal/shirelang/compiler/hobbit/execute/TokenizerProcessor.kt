package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.project.Project
import com.phodal.shirecore.search.tokenizer.*
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc

class TokenizerProcessor {
    companion object {
        fun execute(project: Project, action: PatternActionFunc.Tokenizer): Any {
            if (action.tokType.startsWith("/") && action.tokType.endsWith("/")) {
                val regex = action.tokType.substring(1, action.tokType.length - 1)
                val tokenizer = RegexpTokenizer(
                    object : RegexTokenizerOptions {
                        override val pattern: Regex get() = Regex(regex)
                        override val discardEmpty: Boolean get() = true
                        override val gaps: Boolean? get() = false
                    }
                )

                return tokenizer.tokenize(action.text)
            }

            when (action.tokType) {
                "word" -> {
                    val tokenizer = WordTokenizer()
                    return tokenizer.tokenize(action.text)
                }

                "naming" -> {
                    val tokenizer = CodeNamingTokenizer()
                    return tokenizer.tokenize(action.text)
                }

                "stopwords" -> {
                    return StopwordsBasedTokenizer.instance().tokenize(action.text)
                }

                else -> {
                    val tokenizer = WordTokenizer()
                    return tokenizer.tokenize(action.text)
                }
            }
        }
    }
}
