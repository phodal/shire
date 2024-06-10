package com.phodal.shirelang.pattern

sealed class PatternProcessor {
    abstract fun process(input: List<String>): List<String>
}

class Grep(private val pattern: String) : PatternProcessor() {
    override fun process(input: List<String>): List<String> {
        return input.filter { it.contains(pattern) }
    }
}

class Sort : PatternProcessor() {
    override fun process(input: List<String>): List<String> {
        return input.sorted()
    }
}

class Xargs(private val command: (String) -> Unit) : PatternProcessor() {
    override fun process(input: List<String>): List<String> {
        input.forEach { command(it) }
        return emptyList()
    }
}

class Pipeline(private val processors: List<PatternProcessor>) {
    fun execute(input: List<String>): List<String> {
        return processors.fold(input) { acc, processor -> processor.process(acc) }
    }
}
