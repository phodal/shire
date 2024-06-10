package com.phodal.shirelang.pattern

interface PatternAction {
    fun execute(input: List<String>): List<String>
}

/**
 * Filter the input lines by the given pattern.
 */
class Grep(private val pattern: String) : PatternAction {
    override fun execute(input: List<String>): List<String> {
        return input.filter { it.contains(pattern) }
    }
}

/**
 * Sort the input lines.
 */
class Sort : PatternAction {
    override fun execute(input: List<String>): List<String> {
        return input.sorted()
    }
}

/**
 * Execute the given command for each input line.
 */
class Xargs(private val command: (String) -> Unit) : PatternAction {
    override fun execute(input: List<String>): List<String> {
        input.forEach(command)
        return emptyList()
    }
}
