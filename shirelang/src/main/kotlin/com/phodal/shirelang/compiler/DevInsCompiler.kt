package com.phodal.shirelang.compiler

import com.phodal.shirelang.compiler.error.SHIRE_ERROR
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.phodal.shirelang.compile.VariableTemplateCompiler
import com.phodal.shirelang.compiler.exec.*
import com.phodal.shirelang.completion.dataprovider.BuiltinCommand
import com.phodal.shirelang.completion.dataprovider.CustomCommand
import com.phodal.shirelang.completion.dataprovider.ToolHubVariable
import com.phodal.shirelang.parser.CodeBlockElement
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.psi.ShireTypes
import com.phodal.shirelang.psi.ShireUsed
import kotlinx.coroutines.runBlocking

val CACHED_COMPILE_RESULT = mutableMapOf<String, ShireCompiledResult>()

/**
 * ShireCompiler class is responsible for compiling Shire files by processing different elements such as text segments, newlines, code blocks, used commands, comments, agents, variables, and builtin commands.
 * It takes a Project, ShireFile, Editor, and PsiElement as input parameters.
 * The compile() function processes the elements in the ShireFile and generates a ShireCompiledResult object containing the compiled output.
 * The processUsed() function handles the processing of used commands, agents, and variables within the ShireFile.
 * The processingCommand() function executes the specified builtin command with the provided properties and updates the output accordingly.
 * The lookupNextCode() function looks up the next code block element following a used command.
 * The lookupNextTextSegment() function looks up the next text segment following a used command.
 */
class ShireCompiler(
    private val myProject: Project,
    private val file: ShireFile,
    private val editor: Editor? = null,
    private val element: PsiElement? = null
) {
    private var skipNextCode: Boolean = false
    private val logger = logger<ShireCompiler>()
    private val result = ShireCompiledResult()
    private val output: StringBuilder = StringBuilder()

    /**
     * @return ShireCompiledResult object containing the compiled result
     */
    fun compile(): ShireCompiledResult {
        result.input = file.text
        file.children.forEach {
            when (it.elementType) {
                ShireTypes.TEXT_SEGMENT -> output.append(it.text)
                ShireTypes.NEWLINE -> output.append("\n")
                ShireTypes.CODE -> {
                    if (skipNextCode) {
                        skipNextCode = false
                        return@forEach
                    }

                    output.append(it.text)
                }

                ShireTypes.USED -> processUsed(it as ShireUsed)
                ShireTypes.COMMENTS -> {
                    if (it.text.startsWith("[flow]:")) {
                        val fileName = it.text.substringAfter("[flow]:").trim()
                        val content =
                            myProject.guessProjectDir()?.findFileByRelativePath(fileName)?.let { virtualFile ->
                                virtualFile.inputStream.bufferedReader().use { reader -> reader.readText() }
                            }

                        if (content != null) {
                            val shireFile = ShireFile.fromString(myProject, content)
                            result.nextJob = shireFile
                        }
                    }
                }

                else -> {
                    output.append(it.text)
                    logger.warn("Unknown element type: ${it.elementType}")
                }
            }
        }

        result.output = output.toString()

        CACHED_COMPILE_RESULT[file.name] = result
        return result
    }

    private fun processUsed(used: ShireUsed) {
        val firstChild = used.firstChild
        val id = firstChild.nextSibling

        when (firstChild.elementType) {
            ShireTypes.COMMAND_START -> {
                val command = BuiltinCommand.fromString(id?.text ?: "")
                if (command == null) {
                    CustomCommand.fromString(myProject, id?.text ?: "")?.let { cmd ->
                        ShireFile.fromString(myProject, cmd.content).let { file ->
                            ShireCompiler(myProject, file).compile().let {
                                output.append(it.output)
                                result.hasError = it.hasError
                            }
                        }

                        return
                    }


                    output.append(used.text)
                    logger.warn("Unknown command: ${id?.text}")
                    result.hasError = true
                    return
                }

                if (!command.requireProps) {
                    processingCommand(command, "", used, fallbackText = used.text)
                    return
                }

                val propElement = id.nextSibling?.nextSibling
                val isProp = (propElement.elementType == ShireTypes.COMMAND_PROP)
                if (!isProp) {
                    output.append(used.text)
                    logger.warn("No command prop found: ${used.text}")
                    result.hasError = true
                    return
                }

                processingCommand(command, propElement!!.text, used, fallbackText = used.text)
            }

            ShireTypes.AGENT_START -> {
//                val agentId = id?.text
//                val configs = CustomAgentConfig.loadFromProject(myProject).filter {
//                    it.name == agentId
//                }

//                if (configs.isNotEmpty()) {
//                    result.executeAgent = configs.first()
//                }
                throw NotImplementedError("Not implemented yet")
            }

            ShireTypes.VARIABLE_START -> {
                val variableId = id?.text
                val variable = ToolHubVariable.lookup(myProject, variableId)
                if (variable.isNotEmpty()) {
                    output.append(variable.joinToString("\n") { it })
                    return
                }

                if (editor == null || element == null) {
                    output.append("$SHIRE_ERROR No context editor found for variable: ${used.text}")
                    result.hasError = true
                    return
                }

                val file = element.containingFile
                VariableTemplateCompiler(file.language, file, element, editor).compile(used.text).let {
                    output.append(it)
                }
            }

            else -> {
                logger.warn("Unknown [cc.unitmesh.devti.language.psi.ShireUsed] type: ${firstChild.elementType}")
                output.append(used.text)
            }
        }
    }

    private fun processingCommand(commandNode: BuiltinCommand, prop: String, used: ShireUsed, fallbackText: String) {
        val command: ShireCommand = when (commandNode) {
            BuiltinCommand.FILE -> {
                FileShireCommand(myProject, prop)
            }

            BuiltinCommand.REV -> {
                RevShireCommand(myProject, prop)
            }

            BuiltinCommand.SYMBOL -> {
                result.isLocalCommand = true
                SymbolShireCommand(myProject, prop)
            }

            BuiltinCommand.WRITE -> {
                result.isLocalCommand = true
                val devInCode: CodeBlockElement? = lookupNextCode(used)
                if (devInCode == null) {
                    PrintShireCommand("/" + commandNode.commandName + ":" + prop)
                } else {
                    WriteShireCommand(myProject, prop, devInCode.text, used)
                }
            }

            BuiltinCommand.PATCH -> {
                result.isLocalCommand = true
                val devInCode: CodeBlockElement? = lookupNextCode(used)
                if (devInCode == null) {
                    PrintShireCommand("/" + commandNode.commandName + ":" + prop)
                } else {
                    PatchShireCommand(myProject, prop, devInCode.text)
                }
            }

            BuiltinCommand.COMMIT -> {
                result.isLocalCommand = true
                val devInCode: CodeBlockElement? = lookupNextCode(used)
                if (devInCode == null) {
                    PrintShireCommand("/" + commandNode.commandName + ":" + prop)
                } else {
                    CommitShireCommand(myProject, devInCode.text)
                }
            }

            BuiltinCommand.RUN -> {
                result.isLocalCommand = true
                RunShireCommand(myProject, prop)
            }

            BuiltinCommand.FILE_FUNC -> {
                result.isLocalCommand = true
                FileFuncShireCommand(myProject, prop)
            }

            BuiltinCommand.SHELL -> {
                result.isLocalCommand = true
                ShellShireCommand(myProject, prop)
            }

            BuiltinCommand.BROWSE -> {
                result.isLocalCommand = true
                BrowseShireCommand(myProject, prop)
            }

            BuiltinCommand.Refactor -> {
                result.isLocalCommand = true
                val nextTextSegment = lookupNextTextSegment(used)
                RefactorShireCommand(myProject, prop, nextTextSegment)
            }
        }

        val execResult = runBlocking {
            command.doExecute()
        }

        val isSucceed = execResult?.contains(SHIRE_ERROR) == false
        val result = if (isSucceed) {
            val hasReadCodeBlock = commandNode in listOf(
                BuiltinCommand.WRITE,
                BuiltinCommand.PATCH,
                BuiltinCommand.COMMIT
            )

            if (hasReadCodeBlock) {
                skipNextCode = true
            }

            execResult
        } else {
            execResult ?: fallbackText
        }

        output.append(result)
    }

    private fun lookupNextCode(used: ShireUsed): CodeBlockElement? {
        val devInCode: CodeBlockElement?
        var next: PsiElement? = used
        while (true) {
            next = next?.nextSibling
            if (next == null) {
                devInCode = null
                break
            }

            if (next.elementType == ShireTypes.CODE) {
                devInCode = next as CodeBlockElement
                break
            }
        }

        return devInCode
    }

    private fun lookupNextTextSegment(used: ShireUsed): String {
        val textSegment: StringBuilder = StringBuilder()
        var next: PsiElement? = used
        while (true) {
            next = next?.nextSibling
            if (next == null) {
                break
            }

            if (next.elementType == ShireTypes.TEXT_SEGMENT) {
                textSegment.append(next.text)
                break
            }
        }

        return textSegment.toString()
    }
}


