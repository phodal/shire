package com.phodal.shirecore.action

import com.intellij.openapi.project.Project
import javax.swing.Icon

enum class ShireActionLocation {
    MAIN_MENU,
    CONTEXT_MENU,
    INTENTION_MENU,
    TERMINAL_MENU,
    COMMIT_MENU,
}

data class ShireAction(
    val name: String,
    val description: String,
    val icon: Icon,
    val priority: Int,
    val menuLocation: ShireActionLocation,
    val isAvailable: (project: Project) -> Boolean,
    val action: (project: Project) -> Unit,
)

interface ShireActionRegister {
}