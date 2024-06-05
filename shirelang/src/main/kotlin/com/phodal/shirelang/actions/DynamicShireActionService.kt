package com.phodal.shirelang.actions

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.components.serviceAsync

@Service(Service.Level.APP)
class DynamicShireActionService {
    private val actionCache = mutableMapOf<String, Any>()

    fun putAction(key: String, action: Any) {
        actionCache[key] = action
    }

    fun putAllActions(actions: Map<String, Any>) {
        actionCache.putAll(actions)
    }

    fun getAllActions(): Map<String, Any> {
        return actionCache
    }

    companion object {
        fun getInstance() = ApplicationManager.getApplication().service<DynamicShireActionService>()
        suspend fun getInstanceAsync() = ApplicationManager.getApplication().serviceAsync<DynamicShireActionService>()
    }
}