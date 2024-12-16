package com.phodal.shirecore.utils.markdown

class PostCodeProcessor(
    private val prefixCode: String,
    private val suffixCode: String,
    private val completeCode: String,
    private val indentSize: Int = 4
) {
    private val indent = " ".repeat(indentSize)
    private val javaMethodPattern = Regex("^(?:^|\\s+)(?:@[A-Z]\\w+|(?:(?:public|private|protected)\\s+)?.*\\{)")
    private val endLinePattern = Regex("\n\\s+\n\\s+|\n\\s+\n|\n\n\\s+")

    fun execute(): String {
        if (completeCode.isEmpty()) {
            return completeCode
        }

        var code = completeCode
        val prefix = prefixCode.trim()
        if (completeCode.startsWith(prefix)) {
            code = completeCode.substring(prefix.length)
        }

        var lines: MutableList<String> = code.split("\n").toMutableList()
        val isFirstLineNeedIndent = !prefix.endsWith(indent)

        if (javaMethodPattern.matches(lines[0])) {
            if (isFirstLineNeedIndent && lines[0].startsWith(indent)) {
                lines[0] = lines[0].substring(indent.length)
            }
        }

        if (!lines.last().startsWith(indent)) {
            lines = lines.map { indent + it }.toMutableList()
        }

        val results = lines.joinToString("\n")
        val leftBraceCount = (prefix + code + suffixCode).count { it == '{' }
        val rightBraceCount = (prefix + code + suffixCode).count { it == '}' }

        val reversed = results.reversed()
        var toRemoveBrace = rightBraceCount - leftBraceCount
        val stringBuilder = StringBuilder()

        for (i in reversed.indices) {
            if (toRemoveBrace > 0 && (reversed[i] == '}' || reversed[i] == '\n' || reversed[i] == ' ')) {
                if (reversed[i] == '}') {
                    toRemoveBrace--
                } else {
                    stringBuilder.append(reversed[i])
                }
            } else {
                stringBuilder.append(reversed[i])
            }
        }

        val output = stringBuilder.reverse().toString()
        return endLinePattern.replace(output, "\n")
    }
}
