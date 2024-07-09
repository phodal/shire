package com.phodal.shirecore.template

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase

/**
 * Temp class to store data context for VCS variable actions
 */
data class VcsVariableActionDataContext(val dataContext: DataContext? = null) {
    companion object {
        private val DATA_KEY: Key<VcsVariableActionDataContext> = Key.create(VcsVariableActionDataContext::class.java.name)
        private val dataHolder = UserDataHolderBase()

        fun putData(context: VcsVariableActionDataContext) {
            dataHolder.putUserData(DATA_KEY, context)
        }

        fun getData(): VcsVariableActionDataContext? {
            return dataHolder.getUserData(DATA_KEY)
        }
    }
}