package com.phodal.shirelang.actions.dynamic

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.components.serviceAsync

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

    companion object {
        fun getInstance() = ApplicationManager.getApplication().service<DynamicShireActionService>()
        suspend fun getInstanceAsync() = ApplicationManager.getApplication().serviceAsync<DynamicShireActionService>()
    }
}