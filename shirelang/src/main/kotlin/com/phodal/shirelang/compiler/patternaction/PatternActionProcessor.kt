package com.phodal.shirelang.compiler.patternaction

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findFile
import com.intellij.openapi.vfs.readText
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import java.io.File


class PatternActionProcessor(val myProject: Project, val editor: Editor, val hole: HobbitHole) {
    /**
     * We should execute the variable function with the given key and pipeline functions.
     *
     * Each function output will be the input of the next function.
     */
    fun execute(actionTransform: PatternActionTransform): String {
        var input: Any = ""
        // todo: update rules for input type
        if (actionTransform.pattern.isNotBlank()) {
            input = PatternSearcher.findFilesByRegex(myProject, actionTransform.pattern)
                .map { it.path }
                .toTypedArray()
        }

        return this.execute(actionTransform, input)
    }

    /**
     * This method is used to execute a series of transformations on the input based on the provided PatternActionTransform.
     * The transformations are applied in the order they are defined in the PatternActionTransform.
     * The input can be of any type, but the transformations are applied as if the input is a String.
     * If the input is not a String, it will be converted to a String before applying the transformations.
     * The result of each transformation is used as the input for the next transformation.
     * If the transformation is a Cat, the executeCatFunc method is called with the action and the original input.
     * The result of the last transformation is returned as a String.
     *
     * @param transform The PatternActionTransform that defines the transformations to be applied.
     * @param input The input on which the transformations are to be applied.
     * @return The result of applying the transformations to the input as a String.
     */
    fun execute(transform: PatternActionTransform, input: Any): String {
        var result = input
        transform.patternActionFuncs.forEach { action ->
            result = when (action) {
                is PatternActionFunc.Prompt -> {
                    action.message
                }

                is PatternActionFunc.Grep -> {
                    (result as String).split("\n")
                        .filter { line -> action.patterns.any { line.contains(it) } }
                        .joinToString("\n")
                }

                is PatternActionFunc.Sed -> {
                    (result as String)
                        .replace(action.pattern.toRegex(), action.replacements)
                }

                is PatternActionFunc.Sort -> {
                    (result as String).split("\n")
                        .sorted().joinToString("\n")
                }

                is PatternActionFunc.Uniq -> {
                    (result as String).split("\n").distinct().joinToString("\n")
                }

                is PatternActionFunc.Head -> {
                    (result as String).split("\n").take(action.number.toInt()).joinToString("\n")
                }

                is PatternActionFunc.Tail -> {
                    (result as String).split("\n").takeLast(action.number.toInt()).joinToString("\n")
                }

                is PatternActionFunc.Cat -> {
                    executeCatFunc(action, input)
                }

                is PatternActionFunc.Print -> {
                    action.texts.joinToString("\n")
                }

                is PatternActionFunc.Xargs -> {
                    action.variables
                }
            }
        }

        return result.toString()
    }

    private fun executeCatFunc(action: PatternActionFunc.Cat, input: Any): String {
        val baseDir = myProject.guessProjectDir()!!
        var paths = action.paths
        if (action.paths.isEmpty()) {
            paths = input as Array<String>
        }
        val absolutePaths: List<VirtualFile> = paths.mapNotNull {
            baseDir.findFile(it) ?: LocalFileSystem.getInstance().findFileByIoFile(File(it))
        }

        return absolutePaths.joinToString("\n") { it.readText() }
    }

}