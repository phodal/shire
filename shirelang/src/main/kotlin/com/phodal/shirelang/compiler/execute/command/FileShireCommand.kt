package com.phodal.shirelang.compiler.execute.command

import com.intellij.lang.LanguageCommenters
import com.phodal.shirelang.compiler.ast.LineInfo
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.findFile
import com.phodal.shirecore.lookupFile
import com.phodal.shirecore.relativePath

/**
 * FileAutoCommand is responsible for reading a file and returning its contents.
 *
 * @param myProject the Project in which the file operations are performed
 * @param prop the property string containing the file name and optional line range
 *
 */
class FileShireCommand(private val myProject: Project, private val prop: String) : ShireCommand {
    override suspend fun doExecute(): String? {
        val output = StringBuilder()

        val range: LineInfo? = LineInfo.fromString(prop)

        // prop name can be src/file.name#L1-L2
        val filepath = prop.split("#")[0]
        var virtualFile: VirtualFile? = myProject.lookupFile(filepath)

        if (virtualFile == null) {
            val filename = filepath.split("/").last()
            virtualFile = myProject.findFile(filename)
        }

        val contentsToByteArray = virtualFile?.contentsToByteArray()
        if (contentsToByteArray == null) {
            ShirelangNotifications.warn(myProject, "File not found: $prop")
            /// not show error message to just notify
            return "File not found: $prop"
        }

        contentsToByteArray.let { bytes ->
            val lang = virtualFile.let {
                PsiManager.getInstance(myProject).findFile(it!!)?.language?.displayName
            } ?: ""

            val content = bytes.toString(Charsets.UTF_8)
            val fileContent = if (range != null) {
                val subContent = try {
                    content.split("\n").slice(range.startLine - 1 until range.endLine)
                        .joinToString("\n")
                } catch (e: StringIndexOutOfBoundsException) {
                    content
                }

                subContent
            } else {
                content
            }

            output.append("\n```$lang\n")
            output.append(fileContent)
            output.append("\n```\n")
        }

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

