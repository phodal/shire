package com.phodal.shirecore.search.function

import cc.unitmesh.document.parser.MdDocumentParser
import cc.unitmesh.document.parser.MsOfficeDocumentParser
import cc.unitmesh.document.parser.PdfDocumentParser
import cc.unitmesh.document.parser.TextDocumentParser
import cc.unitmesh.rag.document.DocumentType
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.toNioPathOrNull
import com.phodal.shirecore.search.indices.DiskSynchronizedEmbeddingSearchIndex
import com.phodal.shirecore.search.indices.EmbeddingSearchIndex
import com.phodal.shirecore.search.indices.InMemoryEmbeddingSearchIndex
import com.phodal.shirecore.search.rank.Reranker
import kotlinx.coroutines.*
import java.io.File
import java.nio.file.Path

@Service(Service.Level.PROJECT)
class SemanticService(val project: Project) {
    private var lastQuery: String = ""
    private var lastSearchChunks: List<ScoredText> = emptyList()

    private var index: EmbeddingSearchIndex = InMemoryEmbeddingSearchIndex(cacheDir())
    private val logger = Logger.getInstance(SemanticService::class.java)

    suspend fun embed(chunk: String): FloatArray {
        TODO("see in issue: https://github.com/phodal/shire/issues/84")
    }

    suspend fun embedList(chunk: Array<out String>): List<ScoredText> {
        return chunk.mapIndexed { index, text ->
            ScoredText(
                index = index,
                count = text.length,
                text = text,
                file = null,
                embedding = embed(text)
            )
        }
    }

    suspend fun embedding(chunks: List<ScoredText>): List<ScoredText> {
        val ids = chunks.map { it.text }
        val embeddings = chunks.mapNotNull { entry ->
            entry.embedding ?: run {
                entry.embedding = embed(entry.text)
                entry
            }

            entry.embedding
        }

        index.addEntries(ids zip embeddings)

        index.saveToDisk()
        return chunks
    }

    suspend fun searching(input: String, threshold: Double = 0.5): List<ScoredText> {
        lastQuery = input
        val inputEmbedding = embed(input)
        val findClosest = index.findClosest(inputEmbedding, 10)
        return findClosest.filter { it.similarity > threshold }
    }

    suspend fun reranking(type: String): List<ScoredText> {
        return Reranker.create(type, project).rerank(lastQuery, lastSearchChunks)
    }

    suspend fun splitting(path: List<VirtualFile>): List<ScoredText> =
        withContext(Dispatchers.IO) {
            path.map { file ->
                val inputStream = file.inputStream
                val extension = file.extension ?: return@map emptyList()
                val parser = when (val documentType = DocumentType.of(extension)) {
                    DocumentType.TXT -> TextDocumentParser(documentType)
                    DocumentType.PDF -> PdfDocumentParser()
                    DocumentType.HTML -> TextDocumentParser(documentType)
                    DocumentType.DOC -> MsOfficeDocumentParser(documentType)
                    DocumentType.XLS -> MsOfficeDocumentParser(documentType)
                    DocumentType.PPT -> MsOfficeDocumentParser(documentType)
                    DocumentType.MD -> MdDocumentParser()
                    null -> {
                        if (!file.canBeAdded()) {
                            logger.warn("File ${file.path} can't be added to the index")
                            return@map emptyList()
                        }

                        TextDocumentParser(DocumentType.TXT)
                    }
                }

                parser.parse(inputStream).mapIndexed { index, document ->
                    // filter document.text.length < 0
                    if (document.text.isBlank()) {
                        return@mapIndexed null
                    }

                    ScoredText(
                        text = document.text,
                        embedding = null,
                        index = index,
                        count = document.text.length,
                        file = file,
                    )
                }.filterNotNull()
            }.flatten()
        }

    suspend fun configCache(text: String): Any {
        val type = SemanticStorageType.fromString(text)

        return when (type) {
            SemanticStorageType.MEMORY -> {
                index.loadFromDisk()
            }

            SemanticStorageType.DISK -> {
                if (index is DiskSynchronizedEmbeddingSearchIndex) {
                    index.loadFromDisk()
                } else {
                    index = DiskSynchronizedEmbeddingSearchIndex(cacheDir())

                    index.loadFromDisk()
                }
            }
        }
    }

    private fun cacheDir(): Path {
        return project.guessProjectDir()
            ?.toNioPathOrNull()
            ?.resolve(".shire-cache")
            ?.resolve("semantic") ?: systemPath()
    }

    private fun systemPath(): Path = File(PathManager.getSystemPath())
        .resolve("shire-cache")
        .resolve("semantic").toPath()
}

fun VirtualFile.canBeAdded(): Boolean {
    if (!this.isValid || this.isDirectory) return false
    if (this.fileType.isBinary || FileUtilRt.isTooLarge(this.length)) return false

    return true
}