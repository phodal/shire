package com.phodal.shirecore.search.rank

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.search.function.IndexEntry
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.cancellable

fun RERANK_PROMPT(query: String, documentId: String, document: String): String {
    return """
        You are an expert software developer responsible for helping detect whether the retrieved snippet of code is relevant to the query. For a given input, you need to output a single word: "Yes" or "No" indicating the retrieved snippet is relevant to the query.
        
        Query: Where is the FastAPI server?
        Snippet:
        ```/Users/andrew/Desktop/server/main.py
        from fastapi import FastAPI
        app = FastAPI()
        @app.get("/")
        fun read_root(): Map<String, String> {
            return mapOf("Hello" to "World")
        }
        ```
        Relevant: Yes
        
        Query: Where in the documentation does it talk about the UI?
        Snippet:
        ```/Users/andrew/Projects/bubble_sort/src/lib.rs
        fn bubble_sort<T: Ord>(arr: &mut [T]) {
            for i in 0..arr.size {
                for j in 1 until arr.size - i {
                    if (arr[j - 1] > arr[j]) {
                        arr.swap(j - 1, j)
                    }
                }
            }
        }
        ```
        Relevant: No
        
        Query: $query
        Snippet:
        ```$documentId
        $document
        ```
        Relevant:
    """.trimIndent()
}


@Service(Service.Level.PROJECT)
class LLMReranker(val project: Project) : Reranker {
    override val name = "llmReranker"

    private suspend fun scoreChunk(chunk: IndexEntry, query: String): Double {
        val prompt = RERANK_PROMPT(query, getBasename(chunk.file), chunk.chunk)

        val stream = LlmProvider.provider(project)?.stream(prompt, "", false)!!
        var completion: String = ""
        runBlocking {
            stream.cancellable().collect {
                completion += it
            }
        }

        if (completion.isBlank()) {
            return 0.0
        }

        val answer = completion
            .trim()
            .lowercase()
            .replace("\"", "")
            .replace("'", "")

        return when (answer) {
            "yes" -> 1.0
            "no" -> 0.0
            else -> {
                println("Unexpected response from single token reranker: \"$answer\". Expected \"yes\" or \"no\".")
                0.0
            }
        }
    }

    private fun getBasename(file: VirtualFile?): String {
        return file?.path?.substringAfterLast("/") ?: "unknown"
    }

    override suspend fun rerank(query: String, chunks: List<IndexEntry>): List<Double> {
        return chunks.map { chunk -> scoreChunk(chunk, query) }
    }
}