package com.phodal.shirelang.actions.dynamic

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.keymap.impl.KeymapImpl
import com.phodal.shirecore.config.ShireActionLocation

@Service(Service.Level.APP)
class DynamicShireActionService {
    private val actionCache = mutableMapOf<String, DynamicShireActionConfig>()

    fun putAction(key: String, action: DynamicShireActionConfig) {
        actionCache[key] = action
    }

    fun getAllActions(): List<DynamicShireActionConfig> {
        return actionCache.values.toList()
    }

    fun getAction(location: ShireActionLocation): List<DynamicShireActionConfig> {
        return actionCache.values.filter {
            it.hole?.actionLocation == location
        }
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
        val actionManager: ActionManager = ActionManager.getInstance()
        val actionId = actionManager.getId(action) ?: return

        val keymapManager = KeymapManager.getInstance()
        val activeKeymap = keymapManager.activeKeymap

        if (activeKeymap is KeymapImpl) {
            activeKeymap.removeAllActionShortcuts(actionId)
            activeKeymap.addShortcut(actionId, keyboardShortcut)
        }
    }

    companion object {
        fun getInstance(): DynamicShireActionService =
            ApplicationManager.getApplication().getService(DynamicShireActionService::class.java)
    }
}