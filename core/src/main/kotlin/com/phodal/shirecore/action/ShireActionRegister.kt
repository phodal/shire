package com.phodal.shirecore.action

enum class ShireActionLocation(val location: String) {
    CONTEXT_MENU("ContextMenu"),
    INTENTION_MENU("IntentionMenu"),
    TERMINAL_MENU("TerminalMenu"),
    COMMIT_MENU("CommitMenu"),
    RunPanel("RunPanel")
    ;

    companion object {
        fun from(actionLocation: String): ShireActionLocation {
            return when (actionLocation) {
                "ContextMenu" -> CONTEXT_MENU
                "IntentionMenu" -> INTENTION_MENU
                "TerminalMenu" -> TERMINAL_MENU
                "CommitMenu" -> COMMIT_MENU
                "RunPanel" -> RunPanel
                else -> RunPanel
            }
        }

        fun all(): List<String> {
            return values().map { it.location }
        }

        fun default(): String {
            return CONTEXT_MENU.location
        }
    }
}
