package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.PathUtil.isValidFileName
import com.phodal.shirecore.markdown.CodeFence
import com.phodal.shirecore.middleware.SHIRE_TEMP_OUTPUT
import com.phodal.shirecore.middleware.PostProcessorType
import com.phodal.shirecore.middleware.ShireRunVariableContext
import com.phodal.shirecore.middleware.PostProcessor

class SaveFileProcessor : PostProcessor, Disposable {
    override val processorName: String = PostProcessorType.SaveFile.handleName

    override fun isApplicable(context: ShireRunVariableContext): Boolean = true

    override fun execute(
        project: Project,
        context: ShireRunVariableContext,
        console: ConsoleView?,
        args: List<Any>,
    ): String {
        val fileName: String
        val ext = getFileExt(context)
        if (args.isNotEmpty()) {
            fileName = getValidFileName(args[0].toString(), ext)
            handleForProjectFile(project, fileName, context, console, ext)
        } else {
            fileName = "${System.currentTimeMillis()}.$ext"
            handleForTempFile(project, fileName, context, console)
        }

        return fileName
    }

    private fun getFileExt(context: ShireRunVariableContext): String {
        val language = context.genTargetLanguage ?: PlainTextLanguage.INSTANCE
        return context.genTargetExtension ?: language?.associatedFileType?.defaultExtension ?: "txt"
    }

    private fun handleForTempFile(
        project: Project,
        fileName: String,
        context: ShireRunVariableContext,
        console: ConsoleView?,
    ) {
        ApplicationManager.getApplication().invokeAndWait {
            WriteAction.compute<VirtualFile, Throwable> {
                val outputDir = project.guessProjectDir()?.findChild(SHIRE_TEMP_OUTPUT)
                    ?: project.guessProjectDir()?.createChildDirectory(this, SHIRE_TEMP_OUTPUT)

                val outputFile = outputDir?.createChildData(this, fileName)
                    ?: throw IllegalStateException("Failed to save file")

                val content = getContent(context)
                outputFile.setBinaryContent(content?.toByteArray() ?: ByteArray(0))
                context.pipeData["output"] = outputFile

                project.guessProjectDir()?.refresh(true, true)

                console?.print("Saved to ${outputFile.canonicalPath}\n", ConsoleViewContentType.SYSTEM_OUTPUT)
                outputFile
            }
        }
    }

    private fun handleForProjectFile(
        project: Project,
        filepath: String,
        context: ShireRunVariableContext,
        console: ConsoleView?,
        ext: String,
    ) {
        var fileName = filepath
        ApplicationManager.getApplication().invokeAndWait {
            WriteAction.compute<VirtualFile, Throwable> {
                val projectDir = project.guessProjectDir()
                // if filename starts with / means it's an absolute path, we need to get relative path
                if (fileName.startsWith("/")) {
                    val projectPath = projectDir?.canonicalPath
                    if (projectPath != null) {
                        fileName = fileName.replace(projectPath, "")
                    }
                }

                // filename may include path, like: `src/main/java/HelloWorld.java`, we need to split it
                // first check if the file is already in the project
                var outputFile = projectDir?.findFileByRelativePath(fileName)
                if (outputFile == null) {
                    outputFile = createFile(fileName, projectDir)
                }

                val content = getContent(context)
                outputFile!!.setBinaryContent(content?.toByteArray() ?: ByteArray(0))
                context.pipeData["output"] = outputFile

                projectDir?.refresh(true, true)

                console?.print("Saved to ${outputFile.canonicalPath}", ConsoleViewContentType.SYSTEM_OUTPUT)
                outputFile
            }
        }
    }

    private fun getContent(context: ShireRunVariableContext): String? {
        val outputData = context.pipeData["output"]

        if (outputData is String && outputData.isNotEmpty()) {
            return outputData
        }

        if (context.lastTaskOutput?.isNotEmpty() == true) {
            return context.lastTaskOutput
        }

        return context.genText
    }

    private fun createFile(
        fileName: String,
        projectDir: VirtualFile?,
    ): VirtualFile {
        val path = fileName.split("/").dropLast(1)
        val name = fileName.split("/").last()

        var parentDir = projectDir

        // create directories if not exist
        for (dir in path) {
            parentDir = parentDir?.findChild(dir) ?: parentDir?.createChildDirectory(this, dir)
        }

        val outputFile = parentDir?.createChildData(this, name)
            ?: throw IllegalStateException("Failed to save file")

        return outputFile
    }

    override fun dispose() {
        Disposer.dispose(this)
    }
}

fun getValidFileName(fileName: String, ext: String): String {
    if (fileName.isBlank()) {
        return "${System.currentTimeMillis()}.$ext"
    }

    return if (isValidFileName(fileName)) {
        fileName
    } else if (fileName.contains("`") && fileName.contains("```")) {
        CodeFence.parse(fileName).text
    } else {
        "${System.currentTimeMillis()}.$ext"
    }
}