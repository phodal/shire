package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.phodal.shirecore.lookupFile
import com.phodal.shirelang.completion.dataprovider.BuiltinCommand

class OpenShireCommand(val myProject: Project, private val filename: String) : ShireCommand {
    override val commandName = BuiltinCommand.OPEN

    override suspend fun doExecute(): String? {
        FileDocumentManager.getInstance().saveAllDocuments()

        val file = myProject.lookupFile(filename)
        if (file != null) {
            FileEditorManager.getInstance(myProject).openFile(file, true)
            return "Opening $filename..."
        } else {
            return "File not found: $filename"
        }
    }
}
