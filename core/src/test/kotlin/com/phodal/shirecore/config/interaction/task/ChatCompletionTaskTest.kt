package com.phodal.shirecore.config.interaction.task

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirecore.config.interaction.dto.CodeCompletionRequest
import com.phodal.shirecore.llm.LlmProvider
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File

class ChatCompletionTaskTest : BasePlatformTestCase() {

    @Test
    fun testMultiFileEdits() {
        val project = ProjectManager.getInstance().openProjects.first()
        val file1 = createTestFile(project, "testFile1.txt", "Initial content of file 1")
        val file2 = createTestFile(project, "testFile2.txt", "Initial content of file 2")

        val request = createCodeCompletionRequest(project, file1, "New content for file 1")
        val task = ChatCompletionTask(request)

        runBlocking {
            task.run(createProgressIndicator())
        }

        val updatedContent1 = readFileContent(file1)
        val updatedContent2 = readFileContent(file2)

        assertEquals("New content for file 1", updatedContent1)
        assertEquals("Initial content of file 2", updatedContent2)
    }

    @Test
    fun testIntegrationWithChatPanel() {
        val project = ProjectManager.getInstance().openProjects.first()
        val file = createTestFile(project, "testFile.txt", "Initial content")

        val request = createCodeCompletionRequest(project, file, "New content")
        val task = ChatCompletionTask(request)

        runBlocking {
            task.run(createProgressIndicator())
        }

        val updatedContent = readFileContent(file)
        assertEquals("New content", updatedContent)
    }

    private fun createTestFile(project: Project, fileName: String, content: String): VirtualFile {
        val file = File(project.basePath, fileName)
        file.writeText(content)
        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)!!
    }

    private fun createCodeCompletionRequest(project: Project, file: VirtualFile, newContent: String): CodeCompletionRequest {
        val document = FileDocumentManager.getInstance().getDocument(file)!!
        val editor = EditorFactory.getInstance().createEditor(document, project)
        return CodeCompletionRequest(
            project = project,
            fileUri = file,
            prefixText = "",
            startOffset = 0,
            element = null,
            editor = editor,
            suffixText = newContent,
            isReplacement = true,
            postExecute = { _, _ -> },
            isInsertBefore = false,
            userPrompt = newContent
        )
    }

    private fun createProgressIndicator() = object : com.intellij.openapi.progress.ProgressIndicatorBase() {
        override fun isIndeterminate(): Boolean = false
        override fun setIndeterminate(indeterminate: Boolean) {}
    }

    private fun readFileContent(file: VirtualFile): String {
        return runReadAction {
            val document = FileDocumentManager.getInstance().getDocument(file)!!
            document.text
        }
    }
}
