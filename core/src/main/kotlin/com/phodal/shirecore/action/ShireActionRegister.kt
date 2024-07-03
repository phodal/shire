package com.phodal.shirecore.action

enum class ShireActionLocation(val location: String, val description: String) {
    CONTEXT_MENU("ContextMenu", "Show in Context Menu by Right Click"),
    INTENTION_MENU("IntentionMenu", "Show in Intention Menu by Alt+Enter"),
    TERMINAL_MENU("TerminalMenu", "Show in Terminal panel menu bar"),
    COMMIT_MENU("CommitMenu", "Show in Commit panel menu bar"),
    RunPanel("RunPanel", "Show in Run panel which is the bottom of the IDE")
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

        fun all(): Array<ShireActionLocation> {
            return values()
        }

        fun default(): String {
            return CONTEXT_MENU.location
        }
    }
}
