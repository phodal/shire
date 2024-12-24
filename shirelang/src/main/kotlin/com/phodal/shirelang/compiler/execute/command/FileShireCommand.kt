package com.phodal.shirelang.compiler.execute.command

import com.phodal.shirelang.compiler.ast.LineInfo
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.lookupFile

/**
 * FileAutoCommand is responsible for reading a file and returning its contents.
 *
 * @param myProject the Project in which the file operations are performed
 * @param prop the property string containing the file name and optional line range
 *
 */
class FileShireCommand(private val myProject: Project, private val prop: String) : ShireCommand {
    private val logger = logger<FileShireCommand>()
    private val output = StringBuilder()

    override suspend fun doExecute(): String? {
        // prop name can be src/file.name#L1-L2
        val range: LineInfo? = LineInfo.fromString(prop)
        val virtualFile = file(myProject, prop)

        val contentsToByteArray = virtualFile?.contentsToByteArray()
        if (contentsToByteArray == null) {
            logger.warn("File not found: $virtualFile")
            return null
        }

        contentsToByteArray.let { bytes ->
            val lang = PsiManager.getInstance(myProject).findFile(virtualFile)?.language?.displayName
            val content = bytes.toString(Charsets.UTF_8)

            val fileContent = range?.splitContent(content) ?: content

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

