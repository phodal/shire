package com.phodal.shirecore.action

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

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

        fun default(): String {
            return CONTEXT_MENU.location
        }
    }

    data class ShireAction(
        val name: String,
        val description: String,
        /**
         * Icon path in a project.
         */
        val icon: String?,
        val priority: Int,
        val actionLocation: ShireActionLocation,
        val isAvailable: (project: Project) -> Boolean,
        val action: (project: Project) -> Unit,
    )
}

class ShireActionIcons(val icon: Icon) {

}

abstract class ShireActionRegister {
    abstract val project: Project

    fun loadIcon(path: String): Icon {
        val relativePath = project.guessProjectDir()?.path + "/" + path
        return IconLoader.getIcon(relativePath, ShireActionIcons::class.java)
    }
}
