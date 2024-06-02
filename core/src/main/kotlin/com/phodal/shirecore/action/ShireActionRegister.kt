package com.phodal.shirecore.action

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.IconLoader
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
    /**
     * Icon path in a project.
     */
    val icon: String?,
    val priority: Int,
    val menuLocation: ShireActionLocation,
    val isAvailable: (project: Project) -> Boolean,
    val action: (project: Project) -> Unit,
)

class ShireActionIcons;

abstract class ShireActionRegister {
    abstract val project: Project

    fun loadIcon(path: String): Icon {
        val relativePath = project.guessProjectDir()?.path + "/" + path
        return IconLoader.getIcon(relativePath, ShireActionIcons::class.java)
    }
}
