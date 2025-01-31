package com.phodal.shirelang.completion.dataprovider

import com.intellij.icons.AllIcons
import com.phodal.shirelang.ShireIcons
import java.nio.charset.StandardCharsets
import javax.swing.Icon

enum class BuiltinCommand(
    val commandName: String,
    val description: String,
    val icon: Icon,
    val hasCompletion: Boolean = false,
    val requireProps: Boolean = false,
) {
    FILE("file", "Read the content of a file", AllIcons.Actions.Copy, true, true),
    REV("rev", "Read git change by file", AllIcons.Vcs.History, true, true),

    /**
     * Every language will have a symbol completion, which is the most basic completion, for example,
     * - Java: com.intellij.codeInsight.completion.JavaKeywordCompletion
     * - Kotlin: org.jetbrains.kotlin.idea.completion.KotlinCompletionContributor
     * - Python: com.jetbrains.python.codeInsight.completion.PyClassNameCompletionContributor
     */
    SYMBOL(
        "symbol",
        "Read content by Java/Kotlin canonicalName",
        AllIcons.Toolwindows.ToolWindowStructure,
        true,
        true
    ),
    WRITE("write", "Write content to a file, /write:path/to/file:L1-L2", AllIcons.Actions.Edit, true, true),
    PATCH("patch", "Apply patch to a file, /patch:path/to/file", AllIcons.Vcs.Patch_file, false),
    RUN("run", "Run the content of a file", AllIcons.Actions.Execute, true, true),
    SHELL("shell", "Run shell command", ShireIcons.Terminal, true, true),
    COMMIT("commit", "Commit the content of a file", AllIcons.Vcs.CommitNode, false),
    FILE_FUNC(
        "file-func",
        "Read the name of a file, support for: " + FileFunc.values().joinToString(",") { it.funcName },
        AllIcons.Actions.GroupByFile,
        true,
        true
    ),
    BROWSE(
        "browse",
        "Get the content of a given URL",
        AllIcons.Toolwindows.WebToolWindow,
        true,
        true
    ),
    REFACTOR(
        "refactor",
        "Refactor the content of a file",
        ShireIcons.Idea,
        true,
        true
    ),
    GOTO("goto", "Goto the content of a file", AllIcons.Actions.Forward, true, true),
    STRUCTURE(
        "structure",
        "Get the structure of a file with AST/PSI",
        AllIcons.Toolwindows.ToolWindowStructure,
        true,
        true
    ),
    DATABASE(
        "database",
        "Read the content of a database, /database:query\n```sql\nSELECT * FROM table\n```",
        AllIcons.Toolwindows.ToolWindowHierarchy,
        true,
        true
    ),
    DIR("dir", "List files and directories in a tree-like structure", AllIcons.Actions.ProjectDirectory, true, true),
    LOCAL_SEARCH(
        "localSearch",
        "Search text in the project will return 5 line before and after",
        AllIcons.Actions.Search,
        true,
        true
    ),
    RELATED(
        "related",
        "Get related content by the current file",
        AllIcons.Actions.Find,
        true,
        true
    ),
    OPEN("open", "Open a file in the editor", AllIcons.Actions.MenuOpen, false),
    ;

    companion object {
        fun all(): List<BuiltinCommand> {
            return entries
        }

        fun example(command: BuiltinCommand): String {
            val commandName = command.commandName
            val inputStream = BuiltinCommand::class.java.getResourceAsStream("/docs/agentExamples/$commandName.shire")
                ?: throw IllegalStateException("Example file not found: $commandName.shire")

            return inputStream.use {
                it.readAllBytes().toString(StandardCharsets.UTF_8)
            }
        }

        fun fromString(agentName: String): BuiltinCommand? = values().find { it.commandName == agentName }
    }
}