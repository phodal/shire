package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.phodal.shirecore.findFile
import com.phodal.shirecore.provider.shire.FileRunService
import com.phodal.shirelang.compiler.SHIRE_ERROR
import com.phodal.shirelang.compiler.hobbit.function.ForeignFunction

class ForeignFunctionProcessor {
    companion object {
        fun execute(
            project: Project, funcName: String, args: List<Any>, allVariables: Map<String, Any?>, func: ForeignFunction,
        ): Any {
            val filename = func.funcPath

            val virtualFile = runReadAction {
                project.findFile(filename)
            } ?: return "$SHIRE_ERROR: File not found: $filename"

//            val runService = FileRunService.provider(project, virtualFile)
//            // runInCli
//            return runService?.runFile(project, virtualFile, null)
//                ?: "$SHIRE_ERROR: [ForeignFunctionProcessor] No run service found for file: $filename"

            return FileRunService.runInCli(project, virtualFile)
                ?: "$SHIRE_ERROR: [ForeignFunctionProcessor] No run service found for file: $filename"
        }
    }
}
