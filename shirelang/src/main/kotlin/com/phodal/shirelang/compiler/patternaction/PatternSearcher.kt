package com.phodal.shirelang.compiler.patternaction

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.VirtualFileVisitor
import java.util.regex.Pattern

object PatternSearcher {
    private val cache: MutableMap<String, List<VirtualFile>> = mutableMapOf()

    /**
     * This function is used to find files in a given project that match a list of regular expressions.
     *
     * @param project The project in which to search for files. This is an instance of the Project class.
     * @param regexes A list of regular expressions. The function will return files that match any of these regular expressions.
     *
     * The function uses a cache to improve performance. If the result for a particular regular expression is already in the cache,
     * the function will use the cached result instead of searching the project again.
     *
     * @return A map where the keys are the regular expressions and the values are lists of files that match the corresponding regular expression.
     * If a regular expression does not match any file in the project, the function will return an empty list for that regular expression.
     */
    fun findFilesByMultipleRegex(project: Project, regexes: List<String>): Map<String, List<VirtualFile>> {
        val result: MutableMap<String, List<VirtualFile>> = mutableMapOf()

        for (regex in regexes) {
            if (cache.containsKey(regex)) {
                result[regex] = cache[regex]!!
                continue
            }

            result[regex] = findFilesByRegex(project, regex)
        }

        return result
    }

    /**
     * This function is used to find files in a given project that match a specified regular expression.
     *
     * @param project The project within which to search for files. This is an instance of the Project class.
     * @param regex The regular expression to match file names against. This is a string.
     *
     * The function first checks if the regular expression is already present in the cache. If it is, it returns the corresponding list of files.
     * If the regular expression is not in the cache, the function compiles the regular expression into a pattern.
     * It then refreshes the file system and gets the base directory of the project.
     * If the base directory is not null, it refreshes the file system and finds the file by path.
     * It then creates a visitor for each file in the base directory. If the file name matches the pattern, it is added to the list of matching files.
     * The function finally returns the list of matching files.
     *
     * @return A list of VirtualFile objects that match the specified regular expression. If no matching files are found, an empty list is returned.
     */
    fun findFilesByRegex(project: Project, regex: String): List<VirtualFile> {
        if (cache.containsKey(regex)) {
            return cache[regex]!!
        }

        val matchingFiles: MutableList<VirtualFile> = ArrayList()
        val pattern: Pattern = Pattern.compile(regex)

        VirtualFileManager.getInstance().getFileSystem("file").refresh(false)
        val baseDir: VirtualFile? = project.guessProjectDir()

        if (baseDir != null) {
            baseDir.fileSystem.refreshAndFindFileByPath(baseDir.path)

            VfsUtilCore.visitChildrenRecursively(baseDir, object : VirtualFileVisitor<Any>() {
                override fun visitFile(file: VirtualFile): Boolean {
                    if (pattern.matcher(file.name).matches()) {
                        matchingFiles.add(file)
                    }
                    return true
                }
            })
        }

        return matchingFiles
    }
}