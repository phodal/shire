package com.phodal.shirelang.compiler.hobbit

class VariablePatternFunc(val name: String, val function: PatternFun) {
    fun execute(input: Any): String {
        var result = input
        when(function) {
            is PatternFun.Prompt -> {
                result = function.message
            }
            is PatternFun.Grep -> {
                result = function.patterns.joinToString("\n")
            }
            is PatternFun.Sed -> {
                result = (result as String).replace(function.pattern.toRegex(), function.replacements)
            }
            is PatternFun.Sort -> {
                result = function.arguments.sorted().joinToString("\n")
            }
            is PatternFun.Uniq -> {
                result = function.texts.distinct().joinToString("\n")
            }
            is PatternFun.Head -> {
                result = (result as String).split("\n").take(function.lines.toInt()).joinToString("\n")
            }
            is PatternFun.Tail -> {
                result = (result as String).split("\n").takeLast(function.lines.toInt()).joinToString("\n")
            }

            is PatternFun.Cat -> {
                result = function.paths.joinToString("\n")
            }
            is PatternFun.Print -> {
                result = function.texts.joinToString("\n")
            }
            is PatternFun.Xargs -> {
                result = function.variables
            }
        }

        return result.toString()
    }
}