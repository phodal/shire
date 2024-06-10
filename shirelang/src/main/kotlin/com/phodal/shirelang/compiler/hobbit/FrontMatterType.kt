package com.phodal.shirelang.compiler.hobbit

import com.intellij.openapi.diagnostic.logger


sealed class PatternProcessorItem(val type: String) {
    class Prompt(val message: String): PatternProcessorItem("prompt")
    class Grep(val pattern: String): PatternProcessorItem("grep")
    class Sort: PatternProcessorItem("sort")
    class Xargs(val command: String): PatternProcessorItem("xargs")
    companion object {
        fun from(value: FrontMatterType): List<PatternProcessorItem> {
            return when (value) {
                is FrontMatterType.STRING -> {
                    return listOf(Prompt(value.value as? String ?: ""))
                }
                is FrontMatterType.PATTERN -> {
                    val action = value.value as? ShirePatternAction
                    action?.processors ?: emptyList()
                }
                else -> {
                    logger<PatternProcessorItem>().error("Unknown pattern processor type: $value")
                    emptyList()
                }
            }
        }
    }
}

/**
 * The action location of the action.
 */
class ShirePatternAction(val pattern: String, val processors: List<PatternProcessorItem>)


sealed class FrontMatterType(val value: Any) {
    class STRING(value: String): FrontMatterType(value)
    class NUMBER(value: Int): FrontMatterType(value)
    class DATE(value: String): FrontMatterType(value)
    class BOOLEAN(value: Boolean): FrontMatterType(value)
    class ARRAY(value: List<FrontMatterType>): FrontMatterType(value)
    class OBJECT(value: Map<String, FrontMatterType>): FrontMatterType(value)
    class PATTERN(value: ShirePatternAction): FrontMatterType(value)
}
