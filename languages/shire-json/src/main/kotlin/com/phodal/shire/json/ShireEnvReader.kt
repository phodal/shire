package com.phodal.shire.json

import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex

object ShireEnvReader {
    const val DEFAULT_ENV_NAME = "development"
    /**
     * This function attempts to retrieve a JSON file associated with a given environment name within the specified scope and project.
     *
     * @param envName The name of the environment for which to find the associated JSON file.
     * @param scope The GlobalSearchScope to limit the search for the JSON file.
     * @param project The Project within which to search for the JSON file.
     *
     * @return A JsonFile object if a file with the environment name is found, or null if no such file exists within the given scope and project.
     */
    private fun getEnvJsonFile(
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

    fun getEnvObject(
        envName: String,
        scope: GlobalSearchScope,
        project: Project,
    ): JsonObject? {
        val psiFile = getEnvJsonFile(envName, scope, project)
        val envObject = getEnvObject(envName, psiFile)
        return envObject
    }

    /**
     * Read Shire env file object
     */
    fun getEnvObject(envName: String, psiFile: PsiFile?): JsonObject? {
        val rootObject = (psiFile as? JsonFile)?.topLevelValue as? JsonObject ?: return null
        return rootObject.propertyList.firstOrNull { it.name == envName }?.value as? JsonObject
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
            .filter {
                it != MODEL_LIST && index.getContainingFiles(SHIRE_ENV_ID, it, scope).isNotEmpty()
            }
            .toList()
    }

}