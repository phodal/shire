package com.phodal.shirelang.compiler.hobbit

/**
 * Check variable in [com.phodal.shirelang.completion.provider.ContextVariable] is valid.
 *
 * For example:
 *
 * ```shire
 * ---
 * when: $selection.isEmpty() && $selection.contains("a")
 * ---
 * ```
 */
class VariableCondition(val string: String) {
    private val conditions = mutableListOf<(String) -> Boolean>()

    fun addCondition(condition: (String) -> Boolean) {
        conditions.add(condition)
    }

    fun validate(): Boolean {
        return conditions.all { it(string) }
    }
}

/// length
fun VariableCondition.lengthIs(expected: Int) {
    addCondition { it.length == expected }
}

fun VariableCondition.lengthIsGreaterThan(expected: Int) {
    addCondition { it.length > expected }
}

fun VariableCondition.lengthIsLessThan(expected: Int) {
    addCondition { it.length < expected }
}

/// string
fun VariableCondition.isEmpty() {
    addCondition { it.isEmpty() }
}

fun VariableCondition.isNotEmpty() {
    addCondition { it.isNotEmpty() }
}

fun VariableCondition.contains(substring: String) {
    addCondition { it.contains(substring) }
}

fun VariableCondition.startsWith(prefix: String) {
    addCondition { it.startsWith(prefix) }
}

fun VariableCondition.endsWith(suffix: String) {
    addCondition { it.endsWith(suffix) }
}

// 核心 DSL 函数
fun String.check(block: VariableCondition.() -> Unit): Boolean {
    val conditions = VariableCondition(this)
    conditions.block()
    return conditions.validate()
}
