package com.phodal.shirecore.interaction.task

import com.intellij.openapi.diagnostic.logger
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
    val prompt: String,
    val fileName: String?,
    private val codeOnly: Boolean = false,
    private val taskName: String = ShireCoreBundle.message("intentions.request.background.process.title"),
    val postExecute: ((String) -> Unit)?
) :
    Task.Backgroundable(project, taskName) {
    private val projectRoot = project.guessProjectDir()!!

    override fun run(indicator: ProgressIndicator) {
        val stream = LlmProvider.provider(project)?.stream(prompt, "", false)
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

        val file = project.guessProjectDir()?.toNioPath()?.resolve(inferFileName)?.toFile()
        if (file == null) {
            logger<FileGenerateTask>().error("Failed to create file")
            return
        }
        if (!file.exists()) {
            file.createNewFile()
        }

        if (codeOnly) {
            val code = Code.parse(result).text
            file.writeText(code)
            refreshAndOpenInEditor(file.toPath(), projectRoot)
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