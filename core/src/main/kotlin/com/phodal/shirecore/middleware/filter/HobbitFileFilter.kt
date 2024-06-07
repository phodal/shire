package com.phodal.shirecore.middleware.filter

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import java.util.regex.Pattern

class HobbitFileFilter {
    fun filterFiles(project: Project, globPattern: String, regexPattern: String): List<VirtualFile> {
        val basePath = project.basePath ?: return emptyList()
        val filteredFiles: MutableList<VirtualFile> = ArrayList()
        val globRegex: Regex = globPattern.toRegex()

        ApplicationManager.getApplication().runReadAction {
            val fileTypeManager =
                FileTypeManager.getInstance()
            val fileTypes: Array<FileType> = fileTypeManager.registeredFileTypes

            val regex: Pattern = Pattern.compile(regexPattern)
            VirtualFileManager.getInstance().asyncRefresh {
                val files = VirtualFileManager.getInstance().getFileSystem("file")
                    .findFileByPath(basePath)!!
                    .children

                for (file in files) {
                    for (fileType in fileTypes) {
                        if (fileType.defaultExtension.matches(globRegex) && regex.matcher(file.name).matches()) {
                            filteredFiles.add(file)
                        }
                    }
                }
            }
        }

        return filteredFiles
    }
}