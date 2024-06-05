package com.phodal.shirecore.agent

enum class InteractionType {
    ChatPanel,
    AppendCursor,
    AppendCursorStream,
    OutputFile,
    ReplaceSelection,
    ReplaceCurrentFile,
    ;

    companion object {
        fun from(interaction: String): InteractionType {
            return when (interaction) {
                "ChatPanel" -> ChatPanel
                "AppendCursor" -> AppendCursor
                "AppendCursorStream" -> AppendCursorStream
                "OutputFile" -> OutputFile
                "ReplaceSelection" -> ReplaceSelection
                "ReplaceCurrentFile" -> ReplaceCurrentFile
                else -> ChatPanel
            }
        }
    }
}