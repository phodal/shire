package com.phodal.shirecore.provider.http

import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.phodal.shirecore.index.SHIRE_ENV_ID

object ShireEnvReader {
    /**
     * This function attempts to retrieve a JSON file associated with a given environment name within the specified scope and project.
     *
     * @param envName The name of the environment for which to find the associated JSON file.
     * @param scope The GlobalSearchScope to limit the search for the JSON file.
     * @param project The Project within which to search for the JSON file.
     *
     * @return A JsonFile object if a file with the environment name is found, or null if no such file exists within the given scope and project.
     */
    fun getEnvJsonFile(
        envName: String,
        scope: GlobalSearchScope,
        project: Project,
    ): JsonFile? {
        return FileBasedIndex.getInstance().getContainingFiles(SHIRE_ENV_ID, envName, scope)
            .firstOrNull()
            ?.let {
                (PsiManager.getInstance(project).findFile(it) as? JsonFile)
            }
    }

    /**
     * Read Shire env file object
     */
    fun readEnvObject(psiFile: JsonFile?, envName: String): JsonObject? {
        val rootObject = psiFile?.topLevelValue as? JsonObject ?: return null

        val properties: List<JsonProperty> = rootObject.propertyList
        val envObject = properties.firstOrNull { it.name == envName }?.value as? JsonObject
        return envObject
    }

    fun fetchEnvironmentVariables(envName: String, scope: GlobalSearchScope): List<Set<String>> {
        return FileBasedIndex.getInstance().getValues(
            SHIRE_ENV_ID,
            envName,
            scope
        )
    }

    fun getAllEnvironments(project: Project, scope: GlobalSearchScope): Collection<String> {
        val index = FileBasedIndex.getInstance()

        return index.getAllKeys(SHIRE_ENV_ID, project).stream()
            .filter { index.getContainingFiles(SHIRE_ENV_ID, it, scope).isNotEmpty() }
            .toList()
    }

}