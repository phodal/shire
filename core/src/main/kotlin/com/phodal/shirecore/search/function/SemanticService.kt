package com.phodal.shirecore.search.function

import cc.unitmesh.rag.document.Document
import cc.unitmesh.document.parser.MdDocumentParser
import cc.unitmesh.document.parser.MsOfficeDocumentParser
import cc.unitmesh.document.parser.PdfDocumentParser
import cc.unitmesh.document.parser.TextDocumentParser
import cc.unitmesh.rag.document.DocumentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.search.embedding.InMemoryEmbeddingSearchIndex
import java.io.File

data class IndexEntry(
    var index: Int,
    var count: Int,
    var chunk: String,
    val embedding: FloatArray,
)

@Service(Service.Level.APP)
class SemanticService {
    val index = InMemoryEmbeddingSearchIndex(
        File(PathManager.getSystemPath())
            .resolve("shire-semantic-search")
            .resolve("pattern-func").toPath()
    )

    private val logger = Logger.getInstance(SemanticService::class.java)

    private val embedding: LocalEmbedding =
        LocalEmbedding.create() ?: throw IllegalStateException("Can't create embedding")

    suspend fun embedding(chunk: String): FloatArray {
        return embedding.embed(chunk)
    }

    companion object {
        fun getInstance(): SemanticService =
            ApplicationManager.getApplication().getService(SemanticService::class.java)
    }

    fun splitting(path: List<VirtualFile>): List<List<Document>> {
        return path.map { file ->
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

            parser.parse(inputStream)
        }
    }
}

fun VirtualFile.canBeAdded(): Boolean {
    if (!this.isValid || this.isDirectory) return false
    if (this.fileType.isBinary || FileUtilRt.isTooLarge(this.length)) return false

    return true
}