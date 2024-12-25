package com.phodal.shirecore.provider.streaming

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.LLM_LOGGING
import com.phodal.shirecore.LLM_LOGGING_JSONL
import com.phodal.shirecore.ShireConstants
import com.phodal.shirecore.llm.ChatMessage
import com.phodal.shirecore.llm.ChatRole
import com.phodal.shirecore.runner.console.ShireConsoleViewBase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * The `LoggingStreamingService` class is an implementation of the `StreamingServiceProvider` interface.
 * It provides functionality to log streaming data to a file within a project's directory.
 *
 * ### Properties:
 * - `name`: A string that represents the name of the streaming service, initialized to "logging".
 * - `result`: A private string that accumulates the streaming data received.
 */
class LoggingStreamingService : StreamingServiceProvider {
    private lateinit var outputDir: VirtualFile
    override var name: String = "logging"

    private var result: String = ""
    private var userPrompt: String = ""

    override fun onBeforeStreaming(project: Project, userPrompt: String, console: ShireConsoleViewBase?) {
        this.userPrompt = userPrompt
        this.outputDir = ShireConstants.outputDir(project) ?: throw IllegalStateException("Project directory not found")
        if (outputDir.findChild(LLM_LOGGING) == null) {
            ApplicationManager.getApplication().invokeAndWait {
                WriteAction.compute<VirtualFile, Throwable> {
                    outputDir.createChildData(this, LLM_LOGGING)
                }
            }
        } else {
            runInEdt {
                val file = outputDir.findChild(LLM_LOGGING)
                file?.setBinaryContent(ByteArray(0))
            }
        }

        if (outputDir.findChild(LLM_LOGGING_JSONL) == null) {
            ApplicationManager.getApplication().invokeAndWait {
                WriteAction.compute<VirtualFile, Throwable> {
                    outputDir.createChildData(this, LLM_LOGGING_JSONL)
                }
            }
        }
    }

    override fun onStreaming(project: Project, flow: String, args: List<Any>) {
        result += flow

        val virtualFile = outputDir.findChild(LLM_LOGGING)
        val file = virtualFile?.path?.let { java.io.File(it) }
        file?.appendText(flow)
    }

    override fun afterStreamingDone(project: Project) {
        ApplicationManager.getApplication().invokeAndWait {
            WriteAction.compute<VirtualFile, Throwable> {
                val virtualFile = outputDir.createChildData(this, LLM_LOGGING_JSONL)
                val file = java.io.File(virtualFile.path)
                val value: List<ChatMessage> = listOf(
                    ChatMessage(ChatRole.user, userPrompt),
                    ChatMessage(ChatRole.system, result)
                )

                val result = Json.encodeToString<List<ChatMessage>>(value)

                file.appendText(result)
                file.appendText("\n")
                virtualFile
            }
        }
    }
}