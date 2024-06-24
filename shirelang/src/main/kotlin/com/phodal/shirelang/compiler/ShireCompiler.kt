package com.phodal.shirelang.compiler

import com.intellij.lang.parser.GeneratedParserUtilBase.DUMMY_BLOCK
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.phodal.shirecore.agent.CustomAgent
import com.phodal.shirelang.compile.VariableTemplateCompiler
import com.phodal.shirelang.compiler.error.SHIRE_ERROR
import com.phodal.shirelang.compiler.exec.*
import com.phodal.shirelang.completion.dataprovider.BuiltinCommand
import com.phodal.shirelang.completion.dataprovider.CustomCommand
import com.phodal.shirelang.parser.CodeBlockElement
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.psi.ShireFrontMatterHeader
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
    private val element: PsiElement? = null,
) {
    private var skipNextCode: Boolean = false
    private val logger = logger<ShireCompiler>()
    private val result = ShireCompiledResult()
    private val output: StringBuilder = StringBuilder()

    private val symbolTable = SymbolTable()

    companion object {
        const val FLOW_FALG = "[flow]:"
    }

    /**
     * @return ShireCompiledResult object containing the compiled result
     */
    fun compile(): ShireCompiledResult {
        result.input = file.text
        val iterator = file.children.iterator()

        while (iterator.hasNext()) {
            val psiElement = iterator.next()

            when (psiElement.elementType) {
                ShireTypes.TEXT_SEGMENT -> output.append(psiElement.text)
                ShireTypes.NEWLINE -> output.append("\n")
                ShireTypes.CODE -> {
                    if (skipNextCode) {
                        skipNextCode = false
                        continue
                    }

                    output.append(psiElement.text)
                }
                ShireTypes.USED -> processUsed(psiElement as ShireUsed)
                ShireTypes.COMMENTS -> {
                    if (psiElement.text.startsWith(FLOW_FALG)) {
                        val fileName = psiElement.text.substringAfter(FLOW_FALG).trim()
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

                ShireTypes.FRONTMATTER_START -> {
                    val nextElement = PsiTreeUtil.findChildOfType(
                        psiElement.parent, ShireFrontMatterHeader::class.java
                    ) ?: continue
                    result.config = FrontmatterParser.parse(nextElement)
                }

                ShireTypes.FRONT_MATTER_HEADER -> {
                    result.config = FrontmatterParser.parse(psiElement as ShireFrontMatterHeader)
                }

                WHITE_SPACE, DUMMY_BLOCK -> output.append(psiElement.text)
                ShireTypes.VELOCITY_EXPR -> {
                    logger.info("Velocity expression found: ${psiElement.text}")
                }
                else -> {
                    output.append(psiElement.text)
                    logger.warn("Unknown element type: ${psiElement.elementType}")
                }
            }
        }

        result.shireOutput = output.toString()
        result.symbolTable = symbolTable

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
                                output.append(it.shireOutput)
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
                val configs = CustomAgent.loadFromProject(myProject).filter {
                    it.name == id?.text
                }

                if (configs.isNotEmpty()) {
                    result.executeAgent = configs.first()
                }
            }

            ShireTypes.VARIABLE_START -> {
                val variableId = id?.text

                val currentEditor = editor ?: VariableTemplateCompiler.defaultEditor(myProject)
                val currentElement = element ?: VariableTemplateCompiler.defaultElement(myProject, currentEditor)

                if (currentElement == null) {
                    output.append("$SHIRE_ERROR No element found for variable: ${used.text}")
                    result.hasError = true
                    return
                }

                val lineNo = try {
                    val containingFile = currentElement.containingFile
                    val document: Document? = PsiDocumentManager.getInstance(firstChild!!.project).getDocument(containingFile)
                    document?.getLineNumber(firstChild.textRange.startOffset) ?: 0
                } catch (e: Exception) {
                    0
                }

                symbolTable.addVariable(variableId ?: "", SymbolTable.VariableType.String, lineNo)
                output.append(used.text)
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


