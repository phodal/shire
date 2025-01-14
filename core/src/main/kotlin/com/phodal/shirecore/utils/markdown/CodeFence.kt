package com.phodal.shirecore.utils.markdown

import ai.grazie.nlp.utils.length
import com.intellij.lang.Language
import com.phodal.shirecore.utils.markdown.CodeFenceLanguage.findLanguage
import com.phodal.shirecore.utils.markdown.CodeFenceLanguage.lookupFileExt

class CodeFence(
    val ideaLanguage: Language,
    val text: String,
    var isComplete: Boolean,
    val extension: String?,
    val originLanguage: String? = null,
) {
    companion object {
        private var lastTxtBlock: CodeFence? = null
        val shireStartRegex = Regex("<shire>")
        val shireEndRegex = Regex("</shire>")

        fun parse(content: String): CodeFence {
            val markdownRegex = Regex("```([\\w#+\\s]*)")

            val lines = content.replace("\\n", "\n").lines()

            // 检查是否存在 shire 开始标签
            val startMatch = shireStartRegex.find(content)
            if (startMatch != null) {
                val endMatch = shireEndRegex.find(content)
                val isComplete = endMatch != null

                // 提取内容：如果有结束标签就截取中间内容，没有就取整个后续内容
                val shireContent = if (isComplete) {
                    content.substring(startMatch.range.last + 1, endMatch.range.first).trim()
                } else {
                    content.substring(startMatch.range.last + 1).trim()
                }

                return CodeFence(findLanguage("Shire"), shireContent, isComplete, "shire", "Shire")
            }

            // 原有的 Markdown 代码块解析逻辑
            var codeStarted = false
            var codeClosed = false
            var languageId: String? = null
            val codeBuilder = StringBuilder()

            for (line in lines) {
                if (!codeStarted) {
                    val matchResult: MatchResult? = markdownRegex.find(line.trimStart())
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
            val extension =
                language.associatedFileType?.defaultExtension ?: lookupFileExt(languageId ?: "txt")

            return if (trimmedCode.isEmpty()) {
                CodeFence(language, "", codeClosed, extension, languageId)
            } else {
                CodeFence(language, trimmedCode, codeClosed, extension, languageId)
            }
        }

        fun parseAll(content: String): List<CodeFence> {
            val codeFences = mutableListOf<CodeFence>()
            var currentIndex = 0

            val startMatches = shireStartRegex.findAll(content)
            for (startMatch in startMatches) {
                // 处理标签前的文本
                if (startMatch.range.first > currentIndex) {
                    val beforeText = content.substring(currentIndex, startMatch.range.first)
                    if (beforeText.isNotEmpty()) {
                        parseMarkdownContent(beforeText, codeFences)
                    }
                }

                // 处理 shire 标签内容
                val searchRegion = content.substring(startMatch.range.first)
                val endMatch = shireEndRegex.find(searchRegion)
                val isComplete = endMatch != null

                val shireContent = if (isComplete) {
                    searchRegion.substring(startMatch.range.length, endMatch!!.range.first).trim()
                } else {
                    searchRegion.substring(startMatch.range.length).trim()
                }

                codeFences.add(CodeFence(findLanguage("Shire"), shireContent, isComplete, "shire", "Shire"))
                currentIndex = if (isComplete) {
                    startMatch.range.first + endMatch!!.range.last + 1
                } else {
                    content.length
                }
            }

            // 处理最后剩余的内容
            if (currentIndex < content.length) {
                val remainingContent = content.substring(currentIndex)
                parseMarkdownContent(remainingContent, codeFences)
            }

            return codeFences
        }

        private fun parseMarkdownContent(content: String, codeFences: MutableList<CodeFence>) {
            val regex = Regex("```([\\w#+\\s]*)")
            val lines = content.replace("\\n", "\n").lines()

            var codeStarted = false
            var languageId: String? = null
            val codeBuilder = StringBuilder()
            val textBuilder = StringBuilder()

            for (line in lines) {
                if (!codeStarted) {
                    val matchResult = regex.find(line.trimStart())
                    if (matchResult != null) {
                        if (textBuilder.isNotEmpty()) {
                            val textBlock = CodeFence(findLanguage("markdown"), textBuilder.trim().toString(), true, "txt")
                            lastTxtBlock = textBlock
                            codeFences.add(textBlock)
                            textBuilder.clear()
                        }

                        languageId = matchResult.groups[1]?.value
                        codeStarted = true
                    } else {
                        textBuilder.append(line).append("\n")
                    }
                } else {
                    if (line.startsWith("```")) {
                        val codeContent = codeBuilder.trim().toString()
                        val codeFence = parse("```$languageId\n$codeContent\n```")
                        codeFences.add(codeFence)

                        codeBuilder.clear()
                        codeStarted = false

                        languageId = null
                    } else {
                        codeBuilder.append(line).append("\n")
                    }
                }
            }

            val ideaLanguage = findLanguage(languageId ?: "markdown")
            if (textBuilder.isNotEmpty()) {
                val normal = CodeFence(ideaLanguage, textBuilder.trim().toString(), true, null, languageId)
                codeFences.add(normal)
            }

            if (codeStarted) {
                val codeContent = codeBuilder.trim().toString()
                if (codeContent.isNotEmpty()) {
                    val codeFence = parse("```$languageId\n$codeContent\n")
                    codeFences.add(codeFence)
                } else {
                    val defaultLanguage = CodeFence(ideaLanguage, codeContent, false, null, languageId)
                    codeFences.add(defaultLanguage)
                }
            }
        }
    }
}
