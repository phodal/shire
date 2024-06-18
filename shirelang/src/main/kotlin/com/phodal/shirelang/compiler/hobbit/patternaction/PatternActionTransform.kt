package com.phodal.shirelang.compiler.hobbit.patternaction

/**
 * The `PatternActionTransform` class is a utility class in Kotlin that is used to perform various actions based on a provided pattern and action.
 * The class takes a pattern of type `String` and an action of type `PatternAction` as parameters.
 *
 * @property variable This property represents the pattern to be used for the transformation.
 * @property patternActionFuncs This property represents the action to be performed on the input.
 *
 * @constructor This constructor creates a new instance of `PatternActionTransform` with the specified pattern and action.
 *
 * The `execute` function is a member function of this class which is used to perform the action on the input and return the result as a `String`.
 *
 * @param variable This parameter represents the input on which the action is to be performed.
 * @return The result of the action performed on the input as a `String`.
 *
 * The `execute` function iterates over each action in `patternActionFuncs` and performs the corresponding action on the input.
 * The result of each action is stored in the `result` variable which is initially set to the input.
 * The type of action to be performed is determined using a `when` statement that checks the type of each action.
 * The result of the `execute` function is the final value of the `result` variable converted to a `String`.
 *
 * @see PatternAction
 */
class PatternActionTransform(val variable: String, val pattern: String, val patternActionFuncs: List<PatternActionFunc>) {
    fun execute(input: Any): String {
        var result = input
        patternActionFuncs.forEach { action ->
            when (action) {
                is PatternActionFunc.Prompt -> {
                    result = action.message
                }

                is PatternActionFunc.Grep -> {
                    result = action.patterns.joinToString("\n")
                }

                is PatternActionFunc.Sed -> {
                    result = (result as String).replace(action.pattern.toRegex(), action.replacements)
                }

                is PatternActionFunc.Sort -> {
                    result = action.arguments.sorted().joinToString("\n")
                }

                is PatternActionFunc.Uniq -> {
                    result = action.texts.distinct().joinToString("\n")
                }

                is PatternActionFunc.Head -> {
                    result = (result as String).split("\n").take(action.number.toInt()).joinToString("\n")
                }

                is PatternActionFunc.Tail -> {
                    result = (result as String).split("\n").takeLast(action.number.toInt()).joinToString("\n")
                }

                is PatternActionFunc.Cat -> {
                    result = action.paths.joinToString("\n")
                }

                is PatternActionFunc.Print -> {
                    result = action.texts.joinToString("\n")
                }

                is PatternActionFunc.Xargs -> {
                    result = action.variables
                }
            }
        }

        return result.toString()
    }
}