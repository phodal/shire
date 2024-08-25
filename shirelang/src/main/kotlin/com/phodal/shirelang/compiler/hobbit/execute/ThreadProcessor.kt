package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.readText
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.http.HttpHandler
import com.phodal.shirecore.provider.http.HttpHandlerType
import com.phodal.shirecore.provider.shire.FileRunService
import com.phodal.shirelang.utils.lookupFile

object ThreadProcessor {
    fun execute(myProject: Project, fileName: String): String {
        val file = myProject.lookupFile(fileName) ?: return "File not found: $fileName"

        val content = file.readText()

        // todo: waiting for execute
        // if ends with .cURL.sh, try call cURL service
        if (file.name.lowercase().endsWith(".curl.sh")) {
            // call cURL service
            val execute = HttpHandler.provide(HttpHandlerType.CURL)?.execute(myProject, content)
            if (execute != null) {
                return execute
            }
        }

        val psiFile = PsiManager.getInstance(myProject).findFile(file) ?: return "File not found: $fileName"

        return FileRunService.provider(myProject, file)?.runFile(myProject, file, psiFile) ?: "No run service found"
    }

}
