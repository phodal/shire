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
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.search.embedding.DiskSynchronizedEmbeddingSearchIndex
import com.phodal.shirecore.search.embedding.EmbeddingSearchIndex
import com.phodal.shirecore.search.embedding.InMemoryEmbeddingSearchIndex
import kotlinx.coroutines.*
import java.io.File

@Service(Service.Level.PROJECT)
class SemanticService(val project: Project) {
    var index: EmbeddingSearchIndex = InMemoryEmbeddingSearchIndex(
        File(PathManager.getSystemPath())
            .resolve("shire-semantic-search")
            .resolve("pattern-func").toPath()
    )

    private val logger = Logger.getInstance(SemanticService::class.java)

    suspend fun embedding(): Deferred<LocalEmbedding> = coroutineScope {
        async(Dispatchers.IO) {
            LocalEmbedding.create() ?: throw IllegalStateException("Can't create embedding")
        }
    }

    suspend fun embed(chunk: String): FloatArray {
        val embedding = embedding()
        return embedding.await().embed(chunk)
    }

    suspend fun embedList(chunk: Array<out String>): List<IndexEntry> {
        return chunk.mapIndexed { index, text ->
            IndexEntry(
                index = index,
                count = text.length,
                chunk = text,
                file = null,
                embedding = embed(text)
            )
        }
    }

    suspend fun embedding(chunks: List<IndexEntry>): List<IndexEntry> {
        val indexEntries = chunks.map { entry ->
            entry.embedding ?: run {
                val embedding = embed(entry.chunk)
                entry.embedding = embedding
                entry
            }

            index.addEntries(listOf(entry.chunk to entry.embedding!!))
            entry
        }

        index.saveToDisk()
        return indexEntries
    }

    suspend fun searching(embedChunks: List<IndexEntry>, input: String): List<String> {
        val inputEmbedding = embed(input)
        return index.findClosest(inputEmbedding, 10).map {
            "Similarity: ${it.similarity}, Text: ${it.text}"
        }
    }

    suspend fun splitting(path: List<VirtualFile>): List<IndexEntry> =
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

                    IndexEntry(
                        index = index,
                        count = document.text.length,
                        chunk = document.text,
                        file = file,
                        embedding = null
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
                    index = DiskSynchronizedEmbeddingSearchIndex(
                        File(PathManager.getSystemPath())
                            .resolve("shire-semantic-search")
                            .resolve("pattern-func").toPath()
                    )

                    index.loadFromDisk()
                }
            }
        }
    }
}

fun VirtualFile.canBeAdded(): Boolean {
    if (!this.isValid || this.isDirectory) return false
    if (this.fileType.isBinary || FileUtilRt.isTooLarge(this.length)) return false

    return true
}