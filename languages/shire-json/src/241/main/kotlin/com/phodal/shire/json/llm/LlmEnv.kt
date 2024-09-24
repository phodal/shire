package com.phodal.shire.json.llm

import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.phodal.shire.json.MODEL_LIST
import com.phodal.shire.json.MODEL_TITLE
import com.phodal.shire.json.SHIRE_ENV_ID
import com.phodal.shire.json.valueAsString

object LlmEnv {
    private fun configFromFile(modelName: String, psiFile: JsonFile?): JsonObject? {
        val rootObject = psiFile?.topLevelValue as? JsonObject ?: return null
        val envObject = rootObject.propertyList.firstOrNull { it.name == MODEL_LIST }?.value as? JsonArray
        return envObject?.children?.firstOrNull {
            it is JsonObject && it.findProperty(MODEL_TITLE)?.valueAsString(it) == modelName
        } as? JsonObject
    }

    fun configFromFile(modelName: String, scope: GlobalSearchScope, project: Project): JsonObject? {
        val jsonFile = runReadAction {
            FileBasedIndex.getInstance().getContainingFiles(SHIRE_ENV_ID, MODEL_LIST, scope)
                .firstOrNull()
                ?.let {
                    (PsiManager.getInstance(project).findFile(it) as? JsonFile)
                }
        }

        return configFromFile(modelName, jsonFile)
    }
}