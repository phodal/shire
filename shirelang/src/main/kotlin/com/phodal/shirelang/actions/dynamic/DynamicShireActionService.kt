package com.phodal.shirelang.actions.dynamic

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.phodal.shirecore.action.ShireActionLocation

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


    companion object {
        fun getInstance(): DynamicShireActionService =
            ApplicationManager.getApplication().getService(DynamicShireActionService::class.java)
    }
}