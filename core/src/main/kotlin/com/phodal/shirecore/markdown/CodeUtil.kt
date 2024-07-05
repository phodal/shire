package com.phodal.shirecore.markdown

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.PlainTextLanguage

class Code(val ideaLanguage: Language, val text: String, val isComplete: Boolean, val extension: String?) {
    companion object {
        fun parse(content: String): Code {
            val regex = Regex("```([\\w#+\\s]*)")
            // convert content \\n to \n
            val lines = content.replace("\\n", "\n").lines()

            var codeStarted = false
            var codeClosed = false
            var languageId: String? = null
            val codeBuilder = StringBuilder()

            for (line in lines) {
                if (!codeStarted) {
                    val matchResult: MatchResult? = regex.find(line.trimStart())
                    if (matchResult != null) {
                        val substring = matchResult.groups[1]?.value
                        languageId = substring
                        codeStarted = true
                    }
                } else if (line.startsWith("```")) {
                    codeClosed = true
                    break
                } else {
                    codeBuilder.append(line).append("\n")
                }
            }

            var startIndex = 0
            var endIndex = codeBuilder.length - 1

            while (startIndex <= endIndex) {
                if (!codeBuilder[startIndex].isWhitespace()) {
                    break
                }
                startIndex++
            }

            while (endIndex >= startIndex) {
                if (!codeBuilder[endIndex].isWhitespace()) {
                    break
                }
                endIndex--
            }

            var trimmedCode = codeBuilder.substring(startIndex, endIndex + 1).toString()
            val language = findLanguage(languageId ?: "")
            val extension = findExtension(languageId ?: "txt")

            // if content is not empty, but code is empty, then it's a markdown
            if (trimmedCode.isEmpty()) {
                return Code(findLanguage("markdown"), content.replace("\\n", "\n"), codeClosed, extension)
            }

            if (languageId == "shire") {
                trimmedCode = trimmedCode.replace("\\`\\`\\`", "```")
            }

            return Code(language, trimmedCode, codeClosed, extension)
        }


        fun findExtension(languageId: String): String {
            // list common langauge adn return extension
            return when (languageId) {
                "c#" -> "cs"
                "c++" -> "cpp"
                "c" -> "c"
                "java" -> "java"
                "javascript" -> "js"
                "kotlin" -> "kt"
                "python" -> "py"
                "ruby" -> "rb"
                "swift" -> "swift"
                "typescript" -> "ts"
                "markdown" -> "md"
                else -> "txt"
            }
        }


        /**
         * Searches for a language by its name and returns the corresponding [Language] object. If the language is not found,
         * [PlainTextLanguage.INSTANCE] is returned.
         *
         * @param languageName The name of the language to find.
         * @return The [Language] object corresponding to the given name, or [PlainTextLanguage.INSTANCE] if the language is not found.
         */
        fun findLanguage(languageName: String): Language {
            val fixedLanguage = when (languageName) {
                "csharp" -> "c#"
                "cpp" -> "c++"
                else -> languageName
            }

            val languages = Language.getRegisteredLanguages()
            val registeredLanguages = languages
                .filter { it.displayName.isNotEmpty() }

            return registeredLanguages.find { it.displayName.equals(fixedLanguage, ignoreCase = true) }
                ?: PlainTextLanguage.INSTANCE
        }
    }
}