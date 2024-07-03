package com.phodal.shirecore.agent

enum class InteractionType(val description: String) {
    AppendCursor("Append content at the current cursor position"),
    AppendCursorStream("Append content at the current cursor position, stream output"),
    OutputFile("Output to a new file"),
    ReplaceSelection("Replace the currently selected content"),
    ReplaceCurrentFile("Replace the content of the current file"),
    InsertBeforeSelection("Insert content before the currently selected content"),
    RunPanel("Show Result in Run panel which is the bottom of the IDE")
    ;

    companion object {
        fun from(interaction: String): InteractionType {
            return when (interaction) {
                "AppendCursor" -> AppendCursor
                "AppendCursorStream" -> AppendCursorStream
                "OutputFile" -> OutputFile
                "ReplaceSelection" -> ReplaceSelection
                "ReplaceCurrentFile" -> ReplaceCurrentFile
                "InsertBeforeSelection" -> InsertBeforeSelection
                "RunPanel" -> RunPanel
                else -> RunPanel
            }
        }
    }
}