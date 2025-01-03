package com.phodal.shirelang.run

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.impl.RunConfigurationBeforeRunProviderDelegate
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.util.Key

class ShireBeforeRunProviderDelegate : RunConfigurationBeforeRunProviderDelegate {
    private val SHIRE_BEFORE_RUN_TASK_KEY: String = "Shire.BeforeRunTask"
    private val KEY_MAP: MutableMap<String, Key<Boolean>> = HashMap()

    override fun beforeRun(environment: ExecutionEnvironment) {
        val settings = environment.runnerAndConfigurationSettings ?: return
        val configuration = settings.configuration

        if (configuration is ShireConfiguration) {
            val userDataKey = getRunBeforeUserDataKey(configuration)
            configuration.project.putUserData(userDataKey, true)
        }
    }

    private fun getRunBeforeUserDataKey(runConfiguration: RunConfiguration): Key<Boolean> {
        return KEY_MAP.computeIfAbsent(runConfiguration.name) { key: String ->
            Key.create(
                SHIRE_BEFORE_RUN_TASK_KEY + "_" + key
            )
        }
    }

}
