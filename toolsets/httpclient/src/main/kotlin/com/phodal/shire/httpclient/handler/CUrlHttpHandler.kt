package com.phodal.shire.httpclient.handler

import com.intellij.httpClient.http.request.HttpRequestCollectionProvider
import com.intellij.httpClient.http.request.notification.HttpClientWhatsNewContentService
import com.intellij.ide.scratch.ScratchUtil
import com.intellij.ide.scratch.ScratchesSearchScope
import com.intellij.json.psi.JsonFile
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopesCore
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.indexing.FileBasedIndex
import com.phodal.shire.httpclient.converter.CUrlConverter
import com.phodal.shirecore.provider.http.VariableFiller
import com.phodal.shirecore.index.SHIRE_ENV_ID
import com.phodal.shirecore.provider.http.HttpHandler
import com.phodal.shirecore.provider.http.HttpHandlerType
import okhttp3.OkHttpClient

class CUrlHttpHandler : HttpHandler {
    override fun isApplicable(type: HttpHandlerType): Boolean = type == HttpHandlerType.CURL

    override fun execute(
        project: Project,
        content: String,
        variablesName: Array<String>,
        variableTable: MutableMap<String, Any?>
    ): String? {
        val processVariables: Map<String, String> = variablesName.associateWith { (variableTable[it] as? String ?: "") }

        val client = OkHttpClient()
        val request = runReadAction {
            val envName = getAllEnvironments(project, getSearchScope(project)).firstOrNull() ?: "development"
            val enVariables: List<Set<String>> = fetchEnvironmentVariables(project, envName)
            val psiFile =
                FileBasedIndex.getInstance().getContainingFiles(SHIRE_ENV_ID, envName, getSearchScope(project))
                    .firstOrNull()
                    ?.let {
                        (PsiManager.getInstance(project).findFile(it) as? JsonFile)
                    }

            val envObject = VariableFiller.readEnvObject(psiFile, envName)
            CUrlConverter.convert(content, enVariables, processVariables, envObject)
        }

        val response = client.newCall(request).execute()
        return response.body?.string()
    }

    private fun fetchEnvironmentVariables(project: Project, envName: String): List<Set<String>> {
        val variables: List<Set<String>> = FileBasedIndex.getInstance().getValues(
            SHIRE_ENV_ID,
            envName,
            getSearchScope(project)
        )

        return variables
    }

    private fun getAllEnvironments(project: Project, scope: GlobalSearchScope): Collection<String> {
        val index = FileBasedIndex.getInstance()
        val collection = index.getAllKeys(SHIRE_ENV_ID, project).stream()
            .filter { index.getContainingFiles(SHIRE_ENV_ID, it, scope).isNotEmpty() }
            .toList()

        return collection
    }

    private fun getSearchScope(project: Project, contextFile: PsiFile? = null): GlobalSearchScope {
        val projectScope = ProjectScope.getContentScope(project)
        if (contextFile == null) return projectScope

        val context = PsiUtilCore.getVirtualFile(contextFile)
        val whatsNewFile = HttpClientWhatsNewContentService.getInstance().getWhatsNewFileIfCreated()

        if (contextFile.virtualFile == whatsNewFile) {
            HttpRequestCollectionProvider.getCollectionFolder()?.let { folder ->
                return GlobalSearchScopesCore.directoryScope(project, folder, true)
            }
        }

        if (context != null && !ScratchUtil.isScratch(context) && !projectScope.contains(context)) {
            contextFile.parent?.let { parent ->
                return GlobalSearchScopesCore.directoryScope(parent, true)
            }
        }

        if (ScratchUtil.isScratch(context)) {
            return projectScope.uniteWith(ScratchesSearchScope.getScratchesScope(project))
        }

        return projectScope
    }
}
