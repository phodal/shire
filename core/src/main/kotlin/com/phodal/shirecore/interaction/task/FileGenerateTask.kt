package com.phodal.shirecore.interaction.task

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.llm.ChatMessage
import com.phodal.shirecore.llm.ChatRole
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.markdown.Code
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.runBlocking
import java.nio.file.Path
import kotlin.io.path.Path

open class FileGenerateTask(
    @JvmField val project: Project,
    val messages: List<ChatMessage>,
    val fileName: String?,
    private val codeOnly: Boolean = false,
    private val taskName: String = ShireCoreBundle.message("intentions.request.background.process.title"),
    val postExecute: ((String) -> Unit)?
) :
    Task.Backgroundable(project, taskName) {
    private val projectRoot = project.guessProjectDir()!!

    override fun run(indicator: ProgressIndicator) {
        val requestPrompt = messages.filter { it.role == ChatRole.User }.joinToString("\n") { it.content }
        val systemPrompt = messages.filter { it.role == ChatRole.System }.joinToString("\n") { it.content }

        val stream = LlmProvider.provider(project)?.stream(requestPrompt, systemPrompt, false)
            ?: return

        var result = ""
        runBlocking {
            stream.cancellable().collect {
                result += it
            }
        }

        val inferFileName = if (fileName == null) {
            val language = Code.parse(result).language
            val timestamp = System.currentTimeMillis()
            "output-" + timestamp + if (language == PlainTextLanguage.INSTANCE) ".txt" else ".$language"
        } else {
            fileName
        }

        val file = project.guessProjectDir()!!.toNioPath().resolve(inferFileName).toFile()
        if (!file.exists()) {
            file.createNewFile()
        }

        if (codeOnly) {
            val code = Code.parse(result).text
            file.writeText(code)
            refreshAndOpenInEditor(file.toPath(), projectRoot)
            return
        } else {
            file.writeText(result)
            refreshAndOpenInEditor(Path(projectRoot.path), projectRoot)
        }

        postExecute?.invoke(result)
    }

    private fun refreshAndOpenInEditor(file: Path, parentDir: VirtualFile) {
        runBlocking {
            ProgressManager.getInstance().run(object : Modal(project, "Refreshing Project Model", true) {
                override fun run(indicator: ProgressIndicator) {
                    repeat(5) {
                        val virtualFile = LocalFileSystem.getInstance().findFileByNioFile(file)
                        if (virtualFile == null) {
                            VfsUtil.markDirtyAndRefresh(true, true, true, parentDir)
                        } else {
                            try {
                                FileEditorManager.getInstance(project).openFile(virtualFile, true)
                                return
                            } catch (e: Exception) {
                                //
                            }
                        }
                    }
                }
            })
        }
    }
}