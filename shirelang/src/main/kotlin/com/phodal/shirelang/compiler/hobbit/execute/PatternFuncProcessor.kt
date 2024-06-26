package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.invokeAndWaitIfNeeded
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findFile
import com.intellij.openapi.vfs.readText
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc
import java.io.File

open class PatternFuncProcessor(open val myProject: Project, hole: HobbitHole) {
    /**
     * This function `patternFunctionExecute` is used to execute a specific action based on the type of `PatternActionFunc` provided.
     * It takes three parameters: `action`, `input`, and `lastResult`.
     *
     * @param action This is an instance of `PatternActionFunc` which is a sealed class. The function behavior changes based on the type of `PatternActionFunc`.
     * @param input This is a generic parameter which can be of any type. It is used in the `PatternActionFunc.Cat` case.
     * @param lastResult This is a generic parameter which can be of any type. It is used in all cases except `PatternActionFunc.Prompt`, `PatternActionFunc.Cat`, `PatternActionFunc.Print` and `PatternActionFunc.Xargs`.
     *
     * @return The return type is `Any`. The actual return type depends on the type of `PatternActionFunc`. For example, if `PatternActionFunc` is `Prompt`, it returns a `String`. If `PatternActionFunc` is `Grep`, it returns a `String` joined by "\n" from an `Array` or `String` that contains the specified patterns. If `PatternActionFunc` is `Sed`, it returns a `String` joined by "\n" from an `Array` or `String` where the specified pattern has been replaced. If `PatternActionFunc` is `Sort`, it returns a sorted `String` joined by "\n" from an `Array` or `String`. If `PatternActionFunc` is `Uniq`, it returns a `String` joined by "\n" from an `Array` or `String` with distinct elements. If `PatternActionFunc` is `Head`, it returns a `String` joined by "\n" from the first 'n' elements of an `Array` or `String`. If `PatternActionFunc` is `Tail`, it returns a `String` joined by "\n" from the last 'n' elements of an `Array` or `String`. If `PatternActionFunc` is `Cat`, it executes the `executeCatFunc` function. If `PatternActionFunc` is `Print`, it returns a `String` joined by "\n" from the `texts` property of `Print`. If `PatternActionFunc` is `Xargs`, it returns the `variables` property of `Xargs`. If `PatternActionFunc` is `UserCustom`, it logs an error message. If `PatternActionFunc` is of an unknown type, it logs an error message and returns an empty `String`.
     */
    open fun patternFunctionExecute(action: PatternActionFunc, lastResult: Any, input: Any): Any {
        return when (action) {
            is PatternActionFunc.Prompt -> {
                action.message
            }

            is PatternActionFunc.Grep -> {
                when (lastResult) {
                    is Array<*> -> {
                        (lastResult as Array<String>).filter { line -> action.patterns.any { line.contains(it) } }
                            .joinToString("\n")
                    }

                    else -> {
                        (lastResult as String).split("\n")
                            .filter { line -> action.patterns.any { line.contains(it) } }
                            .joinToString("\n")
                    }
                }
            }

            is PatternActionFunc.Sed -> {
                when (lastResult) {
                    is Array<*> -> {
                        (lastResult as Array<String>).joinToString("\n") { line ->
                            line.replace(
                                action.pattern.toRegex(),
                                action.replacements
                            )
                        }
                    }

                    else -> {
                        (lastResult as String).split("\n").joinToString("\n") { line ->
                            line.replace(
                                action.pattern.toRegex(),
                                action.replacements
                            )
                        }
                    }
                }
            }

            is PatternActionFunc.Sort -> {
                when (lastResult) {
                    is Array<*> -> {
                        (lastResult as Array<String>).sorted().joinToString("\n")
                    }

                    else -> {
                        (lastResult as String).split("\n").sorted().joinToString("\n")
                    }
                }
            }

            is PatternActionFunc.Uniq -> {
                when (lastResult) {
                    is Array<*> -> {
                        (lastResult as Array<String>).distinct().joinToString("\n")
                    }

                    else -> {
                        (lastResult as String).split("\n").distinct().joinToString("\n")
                    }
                }
            }

            is PatternActionFunc.Head -> {
                when (lastResult) {
                    is Array<*> -> {
                        (lastResult as Array<String>).take(action.number.toInt()).joinToString("\n")
                    }

                    else -> {
                        (lastResult as String).split("\n").take(action.number.toInt()).joinToString("\n")
                    }
                }
            }

            is PatternActionFunc.Tail -> {
                when (lastResult) {
                    is Array<*> -> {
                        (lastResult as Array<String>).takeLast(action.number.toInt()).joinToString("\n")
                    }

                    else -> {
                        (lastResult as String).split("\n").takeLast(action.number.toInt()).joinToString("\n")
                    }
                }
            }

            is PatternActionFunc.Cat -> {
                cat(action, input)
            }

            is PatternActionFunc.Print -> {
                action.texts.joinToString("\n")
            }

            is PatternActionFunc.Xargs -> {
                action.variables
            }

            is PatternActionFunc.UserCustom -> {
                logger<PatternActionProcessor>().warn("TODO for User custom: ${action.funcName}")
            }

            is PatternActionFunc.ExecuteShire -> {
                ShireRunFileAction.runFile(myProject, action.string)
            }

            is PatternActionFunc.Notify -> {
                ShirelangNotifications.notify(myProject, action.message)
            }

            is PatternActionFunc.From,
            is PatternActionFunc.Select,
            is PatternActionFunc.Where,
            -> {
                logger<PatternActionProcessor>().error("Unknown pattern processor type: ${action.funcName}")
                // do nothing
            }

            is PatternActionFunc.CaseMatch -> {
                action.keyValue.value
            }


            else -> {
                logger<PatternActionProcessor>().error("Unknown pattern processor type: ${action.funcName}")
                ""
            }
        }
    }

    fun cat(action: PatternActionFunc.Cat, input: Any): String {
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