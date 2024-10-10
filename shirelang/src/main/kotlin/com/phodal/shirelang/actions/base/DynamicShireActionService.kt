package com.phodal.shirelang.actions.base

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.project.isInProject
import com.phodal.shirelang.psi.ShireFile
import java.util.*

@Service(Service.Level.APP)
class DynamicShireActionService {
    private val actionCache = WeakHashMap<VirtualFile, DynamicShireActionConfig>()

    fun updateAction(key: ShireFile, action: DynamicShireActionConfig) {
        actionCache[key.containingFile.virtualFile] = action
    }

    fun removeAction(key: ShireFile) = actionCache.keys.removeIf {
        it == key.containingFile.virtualFile
    }

    fun getAllActions(project: Project): List<DynamicShireActionConfig> {
        return this.actionCache.filterKeys {
            it.isValid && project.isInProject(it)
        }.values
            .toList()
            .distinctBy { it.name }
    }

    fun getActions(location: ShireActionLocation): List<DynamicShireActionConfig> {
        return actionCache.values.filter {
            it.hole?.actionLocation == location && it.hole.enabled
        }.distinctBy { it.name }
    }

    /**
     * Sets a keymap shortcut for a specified action ID.
     *
     * This method takes in the action ID of the desired action and a keyboard string representing the shortcut keys to be set.
     * It retrieves the action manager and keymap manager instances, then adds the specified keyboard shortcut to the active keymap.
     *
     * @param action The ID of the action for which the shortcut is being set.
     * @param keyboardShortcut A string representing the keyboard shortcut keys (e.g. "ctrl shift A").
     */
    fun bindShortcutToAction(action: AnAction, keyboardShortcut: KeyboardShortcut) {
        val actionId = ActionManager.getInstance().getId(action) ?: return

        val activeKeymap = KeymapManager.getInstance().activeKeymap

        activeKeymap.removeAllActionShortcuts(actionId)
        activeKeymap.addShortcut(actionId, keyboardShortcut)
    }

    companion object {
        fun getInstance(): DynamicShireActionService =
            ApplicationManager.getApplication().getService(DynamicShireActionService::class.java)
    }
}