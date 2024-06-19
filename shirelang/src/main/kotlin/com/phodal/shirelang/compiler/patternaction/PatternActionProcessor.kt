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
        if (actionTransform.pattern.isNotBlank()) {
            input = PatternSearcher.findFilesByRegex(myProject, actionTransform.pattern)
                .map { it.path }
                .toTypedArray()
        }

        return this.execute(actionTransform, input)
    }

    /**
     * Since we use `class Cat(vararg val paths: String)`, If input is [Collection] should use [Array]
     */
    fun execute(transform: PatternActionTransform, input: Any): String {
        var result = input
        transform.patternActionFuncs.forEach { action ->
            when (action) {
                is PatternActionFunc.Prompt -> {
                    result = action.message
                }

                is PatternActionFunc.Grep -> {
                    result = (result as String).split("\n").filter { line -> action.patterns.any { line.contains(it) } }
                        .joinToString("\n")
                }

                is PatternActionFunc.Sed -> {
                    result = (result as String).replace(action.pattern.toRegex(), action.replacements)
                }

                is PatternActionFunc.Sort -> {
                    result = (result as String).split("\n").sorted().joinToString("\n")
                }

                is PatternActionFunc.Uniq -> {
                    result = (result as String).split("\n").distinct().joinToString("\n")
                }

                is PatternActionFunc.Head -> {
                    result = (result as String).split("\n").take(action.number.toInt()).joinToString("\n")
                }

                is PatternActionFunc.Tail -> {
                    result = (result as String).split("\n").takeLast(action.number.toInt()).joinToString("\n")
                }

                is PatternActionFunc.Cat -> {
                    val baseDir = myProject.guessProjectDir()!!
                    var paths = action.paths
                    if (action.paths.isEmpty()) {
                        paths = input as Array<String>
                    }

                    var absolutePaths: List<VirtualFile> = paths.mapNotNull {
                        baseDir.findFile(it)
                    }
                    if (absolutePaths.isEmpty()) {
                        absolutePaths = paths.mapNotNull {
                            LocalFileSystem.getInstance().findFileByIoFile(File(it))
                        }
                    }

                    val content = absolutePaths.joinToString("\n") { it.readText() }

                    result = content
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