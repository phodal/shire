package com.phodal.shirecore.utils.markdown

import com.intellij.lang.Language

class CodeFence(
    val ideaLanguage: Language,
    val text: String,
    var isComplete: Boolean,
    val extension: String?,
    val originLanguage: String? = null
) {
    companion object {
        private var lastTxtBlock: CodeFence? = null

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
                CodeFence(language, content.replace("\\n", "\n"), codeClosed, extension, languageId)
            } else {
                CodeFence(language, trimmedCode, codeClosed, extension, languageId)
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
                        if (textBuilder.isNotEmpty()) {
                            val textBlock = CodeFence(
                                CodeFenceLanguage.findLanguage("markdown"),
                                textBuilder.trim().toString(),
                                false,
                                "txt"
                            )

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
                    if (lastTxtBlock != null && lastTxtBlock?.isComplete == false) {
                        lastTxtBlock!!.isComplete = true
                    }

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

            val ideaLanguage = CodeFenceLanguage.findLanguage(languageId ?: "markdown")
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

            return codeFences
        }
    }
}
