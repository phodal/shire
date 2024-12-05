package com.phodal.shirecore.utils.markdown

import com.intellij.lang.Language

class CodeFence(
    val ideaLanguage: Language,
    val text: String,
    var isComplete: Boolean,
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
            val language = CodeFenceLanguage.findLanguage(languageId ?: "")
            val extension =
                language.associatedFileType?.defaultExtension ?: CodeFenceLanguage.lookupFileExt(languageId ?: "txt")

            return if (trimmedCode.isEmpty()) {
                CodeFence(
                    CodeFenceLanguage.findLanguage("markdown"),
                    content.replace("\\n", "\n"),
                    codeClosed,
                    extension
                )
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
                                CodeFence(
                                    CodeFenceLanguage.findLanguage("markdown"),
                                    textBuilder.trim().toString(),
                                    true,
                                    "txt",
                                    true
                                )
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
                    CodeFence(CodeFenceLanguage.findLanguage("text"), textBuilder.trim().toString(), true, "txt", true)
                )
            }

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
    }
}
