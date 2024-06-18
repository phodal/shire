package com.phodal.shirelang.compiler.hobbit.patternaction

/**
 * The `PatternActionTransform` class is used to perform various actions based on the provided pattern and action.
 * This class takes a pattern of type `String` and an action of type `PatternAction` as parameters.
 *
 * @property pattern The pattern to be used for the transformation.
 * @property action The action to be performed on the input.
 *
 * @constructor Creates a new instance of `PatternActionTransform` with the specified pattern and action.
 *
 * @see PatternAction
 *
 * The `execute` function is used to perform the action on the input and return the result as a `String`.
 *
 * @param input The input on which the action is to be performed.
 * @return The result of the action performed on the input as a `String`.
 */
class PatternActionTransform(val pattern: String, val action: PatternAction) {
    fun execute(input: Any): String {
        var result = input
        when(action) {
            is PatternAction.Prompt -> {
                result = action.message
            }
            is PatternAction.Grep -> {
                result = action.patterns.joinToString("\n")
            }
            is PatternAction.Sed -> {
                result = (result as String).replace(action.pattern.toRegex(), action.replacements)
            }
            is PatternAction.Sort -> {
                result = action.arguments.sorted().joinToString("\n")
            }
            is PatternAction.Uniq -> {
                result = action.texts.distinct().joinToString("\n")
            }
            is PatternAction.Head -> {
                result = (result as String).split("\n").take(action.lines.toInt()).joinToString("\n")
            }
            is PatternAction.Tail -> {
                result = (result as String).split("\n").takeLast(action.lines.toInt()).joinToString("\n")
            }

            is PatternAction.Cat -> {
                result = action.paths.joinToString("\n")
            }
            is PatternAction.Print -> {
                result = action.texts.joinToString("\n")
            }
            is PatternAction.Xargs -> {
                result = action.variables
            }
        }

        return result.toString()
    }
}