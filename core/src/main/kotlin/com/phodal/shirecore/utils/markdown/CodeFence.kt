package com.phodal.shirecore.utils.markdown

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.PlainTextLanguage

class CodeFence(
    val ideaLanguage: Language,
    val text: String,
    val isComplete: Boolean,
    val extension: String?,
    val isTextBlock: Boolean = false, // 是否为普通文本块
) {
    companion object {
        private val cache = mutableMapOf<String, CodeFence>()

        fun parse(content: String): CodeFence {
            val regex = Regex("```([\\w#+\\s]*)")
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

            val trimmedCode = codeBuilder.trim().toString()
            val language = findLanguage(languageId ?: "")
            val extension = language.associatedFileType?.defaultExtension ?: lookupFileExt(languageId ?: "txt")

            return if (trimmedCode.isEmpty()) {
                CodeFence(findLanguage("markdown"), content.replace("\\n", "\n"), codeClosed, extension)
            } else {
                CodeFence(language, trimmedCode, codeClosed, extension)
            }
        }

        fun parseAll(content: String): List<CodeFence> {
            val codeFences = mutableListOf<CodeFence>()
            val regex = Regex("```([\\w#+\\s]*)")
            val lines = content.replace("\\n", "\n").lines()

            var codeStarted = false
            var languageId: String? = null
            val codeBuilder = StringBuilder()
            val textBuilder = StringBuilder()

            for ((index, line) in lines.withIndex()) {
                if (!codeStarted) {
                    val matchResult = regex.find(line.trimStart())
                    if (matchResult != null) {
                        // 添加之前的普通文本块
                        if (textBuilder.isNotEmpty()) {
                            codeFences.add(
                                CodeFence(findLanguage("markdown"), textBuilder.trim().toString(), true, "txt", true)
                            )
                            textBuilder.clear()
                        }

                        // 开始代码块
                        languageId = matchResult.groups[1]?.value
                        codeStarted = true
                    } else {
                        textBuilder.append(line).append("\n")
                    }
                } else {
                    if (line.startsWith("```")) {
                        // 结束代码块
                        val codeContent = codeBuilder.trim().toString()
                        val cacheKey = "${languageId.orEmpty()}|$codeContent"

                        val codeFence = cache.getOrPut(cacheKey) {
                            parse("```$languageId\n$codeContent\n```")
                        }
                        codeFences.add(codeFence)

                        codeBuilder.clear()
                        codeStarted = false
                    } else {
                        codeBuilder.append(line).append("\n")
                    }
                }
            }

            // 添加最后的普通文本块
            if (textBuilder.isNotEmpty()) {
                codeFences.add(
                    CodeFence(findLanguage("markdown"), textBuilder.trim().toString(), true, "txt", true)
                )
            }

            // 添加未关闭的代码块
            if (codeStarted) {
                val codeContent = codeBuilder.trim().toString()
                val cacheKey = "${languageId.orEmpty()}|$codeContent"

                val codeFence = cache.getOrPut(cacheKey) {
                    parse("```$languageId\n$codeContent")
                }

                codeFences.add(codeFence)
            }

            return codeFences
        }


        private fun lookupFileExt(languageId: String): String {
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
                "sql" -> "sql"
                "plantuml" -> "puml"
                "shell" -> "sh"
                "objective-c" -> "m"
                "objective-c++" -> "mm"
                "go" -> "go"
                "html" -> "html"
                "css" -> "css"
                "dart" -> "dart"
                "scala" -> "scala"
                "rust" -> "rs"
                else -> languageId
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
                "shell" -> "Shell Script"
                "sh" -> "Shell Script"
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