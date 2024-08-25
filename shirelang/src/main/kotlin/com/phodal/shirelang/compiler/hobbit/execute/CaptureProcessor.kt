package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.psi.PsiCapture
import com.phodal.shirelang.utils.lookupFile

object CaptureProcessor {
    fun execute(myProject: Project, fileName: String, nodeType: String): Any {
        // first lookup file in the file system
        val lookupFile = myProject.lookupFile(fileName) ?: return "File not found: $fileName"

        // check it has custom capture function

        // convert to psi
        val psiFile =
            PsiManager.getInstance(myProject).findFile(lookupFile) ?: return "Failed to find PSI file for $fileName"

        val language = psiFile.language

        PsiCapture.provide(language)?.let {
            return it.capture(psiFile.text, nodeType)
        }

        // execute the capture function
        val result = psiFile.children.filter {
            it.node.elementType.toString() == nodeType
        }

        return result
    }
}
