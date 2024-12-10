package com.phodal.shirecore.config.interaction.task

import com.intellij.openapi.application.runInEdt
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
import com.phodal.shirecore.config.interaction.PostFunction
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.utils.markdown.CodeFence
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.runBlocking
import java.nio.file.Path
import kotlin.io.path.Path
import com.intellij.diff.tools.simple.SimpleDiffViewer
import com.intellij.diff.tools.util.TwosideContentPanel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.components.JBPanel
import java.awt.BorderLayout
import javax.swing.JPanel

open class FileGenerateTask(
    @JvmField val project: Project,
    val prompt: String,
    val fileName: String?,
    private val codeOnly: Boolean = false,
    private val taskName: String = ShireCoreBundle.message("intentions.request.background.process.title"),
    postExecute: PostFunction,
) :
    ShireInteractionTask(project, taskName, postExecute) {
    private val projectRoot = project.guessProjectDir()!!

    override fun run(indicator: ProgressIndicator) {
        val stream = LlmProvider.provider(project)?.stream(prompt, "", false)
        if (stream == null) {
            logger<FileGenerateTask>().error("Failed to create stream")
            postExecute?.invoke("", null)
            return
        }

        var result = ""
        runBlocking {
            stream.cancellable().collect {
                result += it
            }
        }

        val inferFileName = if (fileName == null) {
            val language = CodeFence.parse(result).ideaLanguage
            val timestamp = System.currentTimeMillis()
            "output-" + timestamp + if (language == PlainTextLanguage.INSTANCE) ".txt" else ".$language"
        } else {
            fileName
        }

        val file = project.guessProjectDir()?.toNioPath()?.resolve(inferFileName)?.toFile()
        if (file == null) {
            logger<FileGenerateTask>().error("Failed to create file")
            postExecute?.invoke(result, null)
            return
        }
        if (!file.exists()) {
            file.createNewFile()
        }

        if (codeOnly) {
            val code = CodeFence.parse(result).text
            file.writeText(code)
            refreshAndOpenInEditor(file.toPath(), projectRoot)
        } else {
            file.writeText(result)
            refreshAndOpenInEditor(Path(projectRoot.path), projectRoot)
        }

        postExecute?.invoke(result, null)
    }

    private fun refreshAndOpenInEditor(file: Path, parentDir: VirtualFile) = runBlocking {
        ProgressManager.getInstance().run(RefreshProjectModal(file, parentDir))
    }

    inner class RefreshProjectModal(private val file: Path, private val parentDir: VirtualFile) :
        Modal(project, "Refreshing Project Model", true) {
        override fun run(indicator: ProgressIndicator) {
            repeat(5) {
                val virtualFile = LocalFileSystem.getInstance().findFileByNioFile(file)
                if (virtualFile == null) {
                    VfsUtil.markDirtyAndRefresh(true, true, true, parentDir)
                } else {
                    try {
                        runInEdt {
                            FileEditorManager.getInstance(project).openFile(virtualFile, true)
                        }
                        return
                    } catch (e: Exception) {
                        //
                    }
                }
            }
        }
    }

    private fun createDiffViewerPanel(): JPanel {
        val panel = JBPanel<JBPanel<*>>(BorderLayout())
        val contentPanel = TwosideContentPanel()
        val diffViewer = SimpleDiffViewer(project, contentPanel)
        panel.add(diffViewer.component, BorderLayout.CENTER)
        return panel
    }

    private fun addHyperlinkToCompileOutput(output: String): String {
        val hyperlinkRegex = Regex("(https?://\\S+)")
        return output.replace(hyperlinkRegex) { matchResult ->
            "<a href=\"${matchResult.value}\">${matchResult.value}</a>"
        }
    }
}
