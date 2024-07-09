package com.phodal.shirecore.template

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase

/**
 * Temp class to store data context for VCS variable actions
 */
data class VariableActionEventDataHolder(val vcsDataContext: DataContext? = null) {
    companion object {
        private val DATA_KEY: Key<VariableActionEventDataHolder> = Key.create(VariableActionEventDataHolder::class.java.name)
        private val dataHolder = UserDataHolderBase()

        fun putData(context: VariableActionEventDataHolder) {
            dataHolder.putUserData(DATA_KEY, context)
        }

        fun getData(): VariableActionEventDataHolder? {
            return dataHolder.getUserData(DATA_KEY)
        }
    }
}