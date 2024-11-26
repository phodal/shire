package com.phodal.shirecore.config

enum class InteractionType(val description: String) {
    AppendCursor("Append content at the current cursor position"),
    AppendCursorStream("Append content at the current cursor position, stream output"),
    OutputFile("Output to a new file"),
    ReplaceSelection("Replace the currently selected content"),
    ReplaceCurrentFile("Replace the content of the current file"),
    InsertBeforeSelection("Insert content before the currently selected content"),
    RunPanel("Show Result in Run panel which is the bottom of the IDE"),
    OnPaste("Copy the content to the clipboard"),
    RightPanel("Show Result in Right panel which is the right of the IDE")
    ;

    companion object {
        fun from(interaction: String): InteractionType {
            return when (interaction.lowercase()) {
                AppendCursor.name.lowercase() -> AppendCursor
                AppendCursorStream.name.lowercase() -> AppendCursorStream
                OutputFile.name.lowercase() -> OutputFile
                ReplaceSelection.name.lowercase() -> ReplaceSelection
                ReplaceCurrentFile.name.lowercase() -> ReplaceCurrentFile
                InsertBeforeSelection.name.lowercase() -> InsertBeforeSelection
                RunPanel.name.lowercase() -> RunPanel
                OnPaste.name.lowercase() -> OnPaste
                RightPanel.name.lowercase() -> RightPanel
                else -> RunPanel
            }
        }
    }
}