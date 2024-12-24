package com.phodal.shirelang.compiler.execute.command

import com.intellij.lang.LanguageCommenters
import com.phodal.shirelang.compiler.ast.LineInfo
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.io.FileUtil
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

    override suspend fun doExecute(): String? {
        // prop name can be src/file.name#L1-L2
        val virtualFile = file(myProject, prop)
        if (virtualFile == null) {
            logger.warn("File not found: $prop")
            return null
        }

        val content = virtualFile.contentsToByteArray().toString(Charsets.UTF_8)

        val fileContent = LineInfo.fromString(prop)?.splitContent(content) ?: content
        val language = PsiManager.getInstance(myProject).findFile(virtualFile)?.language
        val lang = language
            ?.displayName
            ?: "plaintext"

        val relativePath = FileUtil.getRelativePath(
            myProject.guessProjectDir()!!.toNioPath().toFile(),
            virtualFile.toNioPath().toFile()
        )

        val commentPrefix = language?.let { LanguageCommenters.INSTANCE.forLanguage(it).lineCommentPrefix } ?: "//"

        return buildString {
            append("\n```$lang\n")
            append("$commentPrefix file path $relativePath\n")
            append(fileContent)
            append("\n```\n")
        }
    }

    companion object {
        fun file(project: Project, path: String): VirtualFile? {
            val filename = path.split("#")[0]
            val virtualFile = project.lookupFile(filename)
            return virtualFile
        }
    }
}

