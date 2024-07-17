package com.phodal.shirecore.search


import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileEditor.impl.EditorHistoryManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.phodal.shirecore.search.algorithm.JaccardSimilarity
import com.phodal.shirecore.search.similar.SimilarChunkContext
import java.io.File

class SimilarChunksSearch(private var snippetLength: Int = 60, private var maxRelevantFiles: Int = 20) :
    JaccardSimilarity() {

    fun similarChunksWithPaths(element: PsiElement): SimilarChunkContext {
        val mostRecentFiles = getMostRecentFiles(element)
        val mostRecentFilesRelativePaths = mostRecentFiles.mapNotNull { relativePathTo(it, element) }

        val chunks = extractChunks(element, mostRecentFiles)
        val jaccardSimilarities = computeInputSimilarity(element.text, chunks)

        val similarChunks: List<Pair<String, String>> =
            jaccardSimilarities.mapIndexedNotNull { fileIndex, jaccardList ->
                val maxIndex = jaccardList.indexOf(jaccardList.maxOrNull())
                val targetChunk = chunks[fileIndex][maxIndex]

                if (targetChunk.isNotEmpty()) {
                    mostRecentFilesRelativePaths[fileIndex] to targetChunk
                } else {
                    null
                }
            }

        val (paths, chunksText) = similarChunks.unzip()
        return SimilarChunkContext(element.language, paths, chunksText)
    }

    private fun relativePathTo(relativeFile: VirtualFile, element: PsiElement): String? {
        val fileIndex: ProjectFileIndex = ProjectRootManager.getInstance(element.project).fileIndex
        var contentRoot: VirtualFile? = runReadAction {
            fileIndex.getContentRootForFile(relativeFile)
        }

        if (contentRoot == null) {
            contentRoot = fileIndex.getClassRootForFile(relativeFile)
        }

        return contentRoot?.let { VfsUtilCore.getRelativePath(relativeFile, it, File.separatorChar) }
    }

    private fun extractChunks(element: PsiElement, mostRecentFiles: List<VirtualFile>): List<List<String>> {
        val psiManager: PsiManager = PsiManager.getInstance(element.project)
        return mostRecentFiles.mapNotNull { file ->
            val psiFile = psiManager.findFile(file)
            psiFile?.text
                ?.split("\n", limit = snippetLength)
                ?.filter {
                    !it.trim().startsWith("import ") && !it.trim().startsWith("package ")
                }
        }
    }

    private fun getMostRecentFiles(element: PsiElement): List<VirtualFile> {
        val fileType: FileType = element.containingFile?.fileType ?: return emptyList()

        val recentFiles: List<VirtualFile> = EditorHistoryManager.getInstance(element.project).fileList.filter { file ->
            file.isValid && file.fileType == fileType && file != element.containingFile.virtualFile
        }

        val start = (recentFiles.size - maxRelevantFiles + 1).coerceAtLeast(0)
        val end = (recentFiles.size - 1).coerceAtLeast(0)
        return recentFiles.subList(start, end)
    }

    companion object {
        val INSTANCE: SimilarChunksSearch = SimilarChunksSearch()

        fun createQuery(element: PsiElement, chunkSize: Int = 60): String? {
            if (element.language.displayName.lowercase() == "markdown") {
                return null
            }

            return runReadAction {
                try {
                    val similarChunksSearch = SimilarChunksSearch(chunkSize).similarChunksWithPaths(element)
                    if (similarChunksSearch.paths?.isEmpty() == true || similarChunksSearch.chunks?.isEmpty() == true) {
                        return@runReadAction null
                    }

                    // todo: change to count query by size
                    val query = similarChunksSearch.format()
                    if (query.length < 10) {
                        return@runReadAction null
                    }

                    if (query.length > 1024) {
                        logger<SimilarChunksSearch>().warn("Query size is too large: ${query.length}")
                        // split to 1024
                        return@runReadAction query.substring(0, 1024)
                    }

                    return@runReadAction query
                } catch (e: Exception) {
                    return@runReadAction null
                }
            }
        }
    }
}
