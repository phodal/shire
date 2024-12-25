package com.phodal.shirecore

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vcs.changes.VcsIgnoreManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.ProjectScope


fun Project.lookupFile(path: String): VirtualFile? {
    val projectPath = this.guessProjectDir()?.toNioPath()
    val realpath = projectPath?.resolve(path)
    return VirtualFileManager.getInstance().findFileByUrl("file://${realpath?.toAbsolutePath()}")
}

fun Project.findFile(path: String): VirtualFile? {
    ApplicationManager.getApplication().assertReadAccessAllowed()
    val searchScope = ProjectScope.getProjectScope(this)
    val fileType: FileType = FileTypeManager.getInstance().getFileTypeByFileName(path)
    val allTypeFiles = FileTypeIndex.getFiles(fileType, searchScope)

    for (file in allTypeFiles) {
        if (file.name == path || file.path.endsWith(path)) {
            return file
        }
    }

    return null
}

fun VirtualFile.canBeAdded(project: Project): Boolean {
    if (!this.isValid || this.isDirectory) return false
    if (this.fileType.isBinary || FileUtilRt.isTooLarge(this.length)) return false
    if (FileTypeManager.getInstance().isFileIgnored(this)) return false
    if (isIgnoredByVcs(project, this)) return false

    return true
}

fun VirtualFile.relativePath(project: Project): String {
    val projectDir = project.guessProjectDir()!!.toNioPath().toFile()
    val relativePath = FileUtil.getRelativePath(projectDir, this.toNioPath().toFile())
    return relativePath ?: this.path
}

fun isIgnoredByVcs(project: Project?, file: VirtualFile?): Boolean {
    val ignoreManager = VcsIgnoreManager.getInstance(project!!)
    return ignoreManager.isPotentiallyIgnoredFile(file!!)
}