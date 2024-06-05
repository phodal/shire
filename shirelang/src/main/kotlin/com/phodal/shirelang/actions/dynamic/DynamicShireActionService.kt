package com.phodal.shirelang.actions.dynamic

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.components.serviceAsync
import com.phodal.shirecore.action.ShireActionLocation

@Service(Service.Level.APP)
class DynamicShireActionService {
    private val actionCache = mutableMapOf<String, DynamicShireActionConfig>()

    fun putAction(key: String, action: DynamicShireActionConfig) {
        actionCache[key] = action
    }

    fun putAllActions(actions: Map<String, DynamicShireActionConfig>) {
        actionCache.putAll(actions)
    }

    fun getAllActions(): List<DynamicShireActionConfig> {
        return actionCache.values.toList()
    }

    fun getContextAction(): List<DynamicShireActionConfig> {
        return actionCache.values.filter {
            it.config.actionLocation == ShireActionLocation.CONTEXT_MENU
        }
    }

    fun getIntentAction(): List<DynamicShireActionConfig> {
        return actionCache.values.filter {
            it.config.actionLocation == ShireActionLocation.INTENTION_MENU
        }
    }

    companion object {
        fun getInstance(): DynamicShireActionService =
            ApplicationManager.getApplication().getService(DynamicShireActionService::class.java)
    }
}