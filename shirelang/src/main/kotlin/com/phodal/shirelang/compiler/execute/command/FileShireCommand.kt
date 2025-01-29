package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.psi.PsiManager
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.findFile
import com.phodal.shirecore.lookupFile
import com.phodal.shirelang.compiler.ast.LineInfo

/**
 * FileAutoCommand is responsible for reading a file and returning its contents.
 *
 * @param myProject the Project in which the file operations are performed
 * @param prop the property string containing the file name and optional line range
 *
 */
class FileShireCommand(private val myProject: Project, private val prop: String) : ShireCommand {
    override suspend fun doExecute(): String? {
        val range: LineInfo? = LineInfo.fromString(prop)

        // prop name can be src/file.name#L1-L2
        val filepath = prop.split("#")[0]
        var virtualFile: VirtualFile? = myProject.lookupFile(filepath)

        if (virtualFile == null) {
            val filename = filepath.split("/").last()
            virtualFile = myProject.findFile(filename, false)
        }

        val content = virtualFile?.readText()
        if (content == null) {
            ShirelangNotifications.warn(myProject, "File not found: $prop")
            /// not show error message to just notify
            return "File not found: $prop"
        }

        val lang = PsiManager.getInstance(myProject).findFile(virtualFile)?.language?.displayName ?: ""

        val fileContent = if (range == null) {
            content
        } else {
            try {
                content.split("\n").slice(range.startLine - 1 until range.endLine)
                    .joinToString("\n")
            } catch (e: StringIndexOutOfBoundsException) {
                content
            }
        }

        val output = StringBuilder()
        // add file path
        output.append("// File: $prop\n")
        output.append("\n```$lang\n")
        output.append(fileContent)
        output.append("\n```\n")
        return output.toString()
    }

    companion object {
        fun file(project: Project, path: String): VirtualFile? {
            val filename = path.split("#")[0]
            val virtualFile = project.lookupFile(filename)
            return virtualFile
        }
    }
}

