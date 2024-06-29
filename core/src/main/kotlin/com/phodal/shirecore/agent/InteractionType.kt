package com.phodal.shirecore.agent

enum class InteractionType(val description: String) {
//    ChatPanel("Output results to the chat panel"),
    AppendCursor("Append content at the current cursor position"),
    AppendCursorStream("Append content at the current cursor position, stream output"),
    OutputFile("Output to a file"),
    ReplaceSelection("Replace the currently selected content"),
    ReplaceCurrentFile("Replace the content of the current file"),
    InsertBeforeSelection("Insert content before the currently selected content"),
    ;

    companion object {
        fun from(interaction: String): InteractionType {
            return when (interaction) {
//                "ChatPanel" -> ChatPanel
                "AppendCursor" -> AppendCursor
                "AppendCursorStream" -> AppendCursorStream
                "OutputFile" -> OutputFile
                "ReplaceSelection" -> ReplaceSelection
                "ReplaceCurrentFile" -> ReplaceCurrentFile
                "InsertBeforeSelection" -> InsertBeforeSelection
                else -> AppendCursor
            }
        }
    }
}