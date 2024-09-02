package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findFile
import com.intellij.openapi.vfs.readText
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.extension.read
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.guard.RedactProcessor
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider
import com.phodal.shirecore.search.function.ScoredText
import com.phodal.shirecore.search.function.SemanticService
import com.phodal.shirelang.ShireActionStartupActivity
import com.phodal.shirelang.actions.ShireRunFileAction
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.FrontMatterType
import com.phodal.shirelang.compiler.hobbit.ast.Statement
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc
import java.io.File

open class PatternFuncProcessor(open val myProject: Project, open val hole: HobbitHole) {
    /**
     * This function `patternFunctionExecute` is used to execute a specific action based on the type of `PatternActionFunc` provided.
     * It takes three parameters: `action`, `input`, and `lastResult`.
     *
     * @param action This is an instance of `PatternActionFunc` which is a sealed class. The function behavior changes based on the type of `PatternActionFunc`.
     * @param input This is a generic parameter which can be of any type. It is used in the `PatternActionFunc.Cat` case.
     * @param lastResult This is a generic parameter which can be of any type. It is used in all cases except `PatternActionFunc.Prompt`, `PatternActionFunc.Cat`, `PatternActionFunc.`Print`` and `PatternActionFunc.Xargs`.
     *
     * @return The return type is `Any`. The actual return type depends on the type of `PatternActionFunc`. For example, if `PatternActionFunc` is `Prompt`, it returns a `String`. If `PatternActionFunc` is `Grep`, it returns a `String` joined by "\n" from an `Array` or `String` that contains the specified patterns. If `PatternActionFunc` is `Sed`, it returns a `String` joined by "\n" from an `Array` or `String` where the specified pattern has been replaced. If `PatternActionFunc` is `Sort`, it returns a sorted `String` joined by "\n" from an `Array` or `String`. If `PatternActionFunc` is `Uniq`, it returns a `String` joined by "\n" from an `Array` or `String` with distinct elements. If `PatternActionFunc` is `Head`, it returns a `String` joined by "\n" from the first 'n' elements of an `Array` or `String`. If `PatternActionFunc` is `Tail`, it returns a `String` joined by "\n" from the last 'n' elements of an `Array` or `String`. If `PatternActionFunc` is `Cat`, it executes the `executeCatFunc` function. If `PatternActionFunc` is `Print`, it returns a `String` joined by "\n" from the `texts` property of `Print`. If `PatternActionFunc` is `Xargs`, it returns the `variables` property of `Xargs`. If `PatternActionFunc` is `UserCustom`, it logs an error message. If `PatternActionFunc` is of an unknown type, it logs an error message and returns an empty `String`.
     */
    open suspend fun patternFunctionExecute(
        action: PatternActionFunc,
        lastResult: Any,
        input: Any,
        variableTable: MutableMap<String, Any?> = mutableMapOf(),
    ): Any {
        val semanticService = myProject.getService(SemanticService::class.java)

        return when (action) {
            is PatternActionFunc.Find -> {
                when (lastResult) {
                    is Array<*> -> {
                        (lastResult as Array<String>)
                            .filter { line -> line.contains(action.text) }
                            .joinToString("\n")
                    }

                    else -> {
                        (lastResult as String).split("\n")
                            .filter { line -> line.contains(action.text) }
                            .joinToString("\n")
                    }
                }
            }

            is PatternActionFunc.Grep -> {
                val regexs = action.patterns.map { it.toRegex() }
                when (lastResult) {
                    is Array<*> -> {
                        val inputArray = (lastResult as Array<String>)
                        val result = regexs.map { regex ->
                            inputArray.map { line ->
                                regex.findAll(line)
                                    .map {
                                        if (it.groupValues.size > 1) {
                                            it.groupValues[1]
                                        } else {
                                            it.groupValues[0]
                                        }
                                    }.toList()
                            }.flatten()
                        }.flatten().joinToString("\n")

                        result
                    }

                    is String -> {
                        val result = regexs.map { regex ->
                            regex.findAll(lastResult)
                                .map {
                                    if (it.groupValues.size > 1) {
                                        it.groupValues[1]
                                    } else {
                                        it.groupValues[0]
                                    }
                                }.toList()
                        }.flatten().joinToString("\n")

                        result
                    }

                    else -> {
                        logger<PatternActionProcessor>().error("Unknown pattern input for ${action.funcName}, lastResult: $lastResult")
                        ""
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
                val path: Array<String> = action.paths.map {
                    if (it.startsWith("\$")) {
                        variableTable[it.substring(1)]?.toString() ?: it
                    } else {
                        it
                    }
                }.toTypedArray()

                cat(path, input)
            }

            is PatternActionFunc.Print -> {
                if (action.texts.isEmpty()) {
                    when (lastResult) {
                        is Array<*> -> {
                            return (lastResult as Array<String>).joinToString("\n")
                        }

                        else -> {
                            return lastResult.toString()
                        }
                    }
                }
                action.texts.map {
                    if (it.startsWith("\$")) {
                        variableTable[it.substring(1)] ?: it
                    } else {
                        it
                    }
                }.joinToString("\n")
            }

            is PatternActionFunc.Xargs -> {
                action.variables
            }

            is PatternActionFunc.ToolchainFunction -> {
                val args: MutableList<Any> = action.args.toMutableList()
                /// add lastResult at args first
                when(lastResult) {
                    is String -> {
                        args.add(0, lastResult)
                    }
                    is List<*> -> {
                        if (lastResult.isNotEmpty()) {
                            args.add(0, lastResult)
                        }
                    }
                    is Array<*> -> {
                        if (lastResult.isNotEmpty()) {
                            args.add(0, lastResult)
                        }
                    }
                    else -> {
                        args.add(0, lastResult)
                    }
                }

                ToolchainFunctionProvider.provide(myProject, action.funcName)
                    ?.execute(myProject, action.funcName, args, variableTable)
                    ?: logger<PatternActionProcessor>().error("TODO for User custom: ${action.funcName}")
            }

            is PatternActionFunc.Notify -> {
                ShirelangNotifications.info(myProject, action.message)
            }

            is PatternActionFunc.From,
            is PatternActionFunc.Select,
            is PatternActionFunc.Where,
                -> {
                logger<PatternActionProcessor>().error("Unknown pattern processor type: ${action.funcName}")
            }

            is PatternActionFunc.CaseMatch -> {
                val actions = evaluateCase(action, input) ?: return ""
                FunctionStatementProcessor(myProject, hole)
                    .execute(actions.value as Statement, mutableMapOf("output" to parseInput(input)))
                    .toString()
            }

            is PatternActionFunc.Embedding -> {
                var result: List<ScoredText> = mutableListOf()
                if (lastResult is List<*>) {
                    if (lastResult.isNotEmpty() && lastResult.first() is ScoredText) {
                        result = semanticService.embedding(lastResult as List<ScoredText>)
                    } else {
                        result = semanticService.embedList(action.entries)
                    }
                }


                result
            }

            is PatternActionFunc.Splitting -> {
                semanticService.splitting(resolvePaths(action.paths, input))
            }

            is PatternActionFunc.Searching -> {
                semanticService.searching(action.text, action.threshold)
            }

            is PatternActionFunc.Caching -> {
                semanticService.configCache(action.text)
            }

            is PatternActionFunc.Reranking -> {
                semanticService.reranking(action.type)
            }

            is PatternActionFunc.Redact -> {
                RedactProcessor.execute(myProject, lastResult)
            }

            is PatternActionFunc.Crawl -> {
                val urls: MutableList<String> = mutableListOf()
                if (action.urls.isEmpty()) {
                    when (lastResult) {
                        is ArrayList<*> -> {
                            (lastResult as ArrayList<String>).forEach {
                                urls.add(it)
                            }
                        }

                        is String -> {
                            lastResult.split("\n").forEach {
                                urls.add(it)
                            }
                        }

                        else -> {
                            logger<FunctionStatementProcessor>().warn("crawl error: $lastResult")
                        }
                    }
                } else {
                    urls.addAll(action.urls)
                }

                val finalUrls = urls.map { it.trim() }.filter { it.isNotEmpty() }
                CrawlProcessor.execute(finalUrls.toTypedArray())
            }

            is PatternActionFunc.Capture -> {
                CaptureProcessor.execute(myProject, action.fileName, action.nodeType)
            }

            is PatternActionFunc.ExecuteShire -> {
                // remove $ for all variableName
                val variables: Array<String> = action.variableNames.map {
                    if (it.startsWith("\$")) {
                        it.substring(1)
                    } else {
                        it
                    }
                }.toTypedArray()

                try {
                    val file = runReadAction {
                        ShireActionStartupActivity.obtainShireFiles(myProject).find {
                            it.name == action.filename
                        }
                    }

                    if (file == null) {
                        logger<FunctionStatementProcessor>().warn("execute shire error: file not found")
                        return ""
                    }

                    ShireRunFileAction.suspendExecuteFile(myProject, variables, variableTable, file) ?: ""
                } catch (e: Exception) {
                    logger<FunctionStatementProcessor>().warn("execute shire error: $e")
                }
            }

            is PatternActionFunc.Thread -> {
                // remove $ for all variableName
                val varNames = action.variableNames.toMutableList().apply {
                    if (!contains("output")) {
                        add("output")
                    }
                }.map {
                    if (it.startsWith("\$")) {
                        it.substring(1)
                    } else {
                        it
                    }
                }.toTypedArray()

                if (!variableTable.containsKey("output")) {
                    variableTable["output"] = lastResult
                }


                ThreadProcessor.execute(myProject, action.fileName, varNames, variableTable)
            }

            is PatternActionFunc.JsonPath -> {
                val jsonStr = action.obj ?: lastResult as String

                val result: String = try {
                    JsonPath.parse(jsonStr)?.read<Any>(action.path.trim()).toString()
                } catch (e: Exception) {
                    logger<FunctionStatementProcessor>().warn("jsonpath error: $e")
                    return jsonStr
                }

                if (result == "null") {
                    logger<FunctionStatementProcessor>().warn("jsonpath error: $result for $jsonStr")
                    return jsonStr
                }

                result
            }
        }
    }

    private fun evaluateCase(action: PatternActionFunc.CaseMatch, input: Any): FrontMatterType.EXPRESSION? {
        var fitCondition = action.keyValue.firstOrNull { it.key.toValue() == parseInput(input) }
        if (fitCondition == null) {
            fitCondition = action.keyValue.firstOrNull { it.key.toValue() == "default" }
        }

        return fitCondition?.value
    }

    private fun parseInput(input: Any): String {
        return when (input) {
            is String -> {
                input
            }

            is Array<*> -> {
                input.firstOrNull().toString()
            }

            else -> {
                input.toString()
            }
        }
    }

    fun cat(paths: Array<String>, input: Any): String {
        val absolutePaths: List<VirtualFile> = resolvePaths(paths, input)
        return absolutePaths.joinToString("\n") { it.readText() }
    }

    /**
     * @param userPaths The paths provided by the user in the script: `cat("file1.txt", "file2.txt")`.
     * @param patterMatchPaths The paths provided by the pattern match: `/.*.txt/ { cat } `.
     */
    private fun resolvePaths(userPaths: Array<out String>, patterMatchPaths: Any): List<VirtualFile> {
        val baseDir = myProject.guessProjectDir()!!
        var paths = userPaths
        if (userPaths.isEmpty()) {
            paths = patterMatchPaths as Array<String>
        }

        val absolutePaths: List<VirtualFile> = paths.mapNotNull {
            baseDir.findFile(it) ?: try {
                LocalFileSystem.getInstance().findFileByIoFile(File(it))
            } catch (e: Exception) {
                null
            }
        }

        return absolutePaths
    }
}
