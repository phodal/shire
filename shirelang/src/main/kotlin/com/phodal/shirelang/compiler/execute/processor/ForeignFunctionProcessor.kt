package com.phodal.shirelang.compiler.execute.processor

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.phodal.shirecore.findFile
import com.phodal.shirecore.provider.shire.FileRunService
import com.phodal.shirelang.compiler.parser.SHIRE_ERROR
import com.phodal.shirelang.compiler.ast.ForeignFunction

class ForeignFunctionProcessor {
    companion object {
        fun execute(
            project: Project, funcName: String, args: List<Any>, allVariables: Map<String, Any?>, func: ForeignFunction,
        ): Any {
            val filename = func.funcPath

            val virtualFile = runReadAction {
                project.findFile(filename)
            } ?: return "$SHIRE_ERROR: File not found: $filename"


            /// last args will be file path, should be skip
            val args: List<String> = args.dropLast(1).map {
                // handle for arrayList and map type
                when (it) {
                    is List<*> -> it.joinToString(",")
                    is Map<*, *> -> it.entries.joinToString(",") { (k, v) -> "$k=$v" }
                    else -> it.toString()
                }
            }

            return FileRunService.runInCli(project, virtualFile, args)
                ?: "$SHIRE_ERROR: [ForeignFunctionProcessor] No run service found for file: $filename"
        }
    }
}
