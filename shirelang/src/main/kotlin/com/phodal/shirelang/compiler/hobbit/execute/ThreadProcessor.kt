package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.project.Project
import com.phodal.shirelang.utils.lookupFile

object ThreadProcessor {
    fun execute(myProject: Project, fileName: String): Any {
        val lookupFile = myProject.lookupFile(fileName) ?: return "File not found: $fileName"

        // todo: waiting for execute

        // lookup bash execute in async

        return lookupFile
    }

}
