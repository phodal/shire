package com.phodal.shire.httpclient.handler

import com.intellij.httpClient.http.request.HttpRequestCollectionProvider
import com.intellij.httpClient.http.request.notification.HttpClientWhatsNewContentService
import com.intellij.ide.scratch.ScratchUtil
import com.intellij.ide.scratch.ScratchesSearchScope
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopesCore
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.util.PsiUtilCore
import com.phodal.shire.httpclient.converter.CUrlConverter
import com.phodal.shirecore.provider.http.HttpHandler
import com.phodal.shirecore.provider.http.HttpHandlerType
import com.phodal.shirecore.provider.http.ShireEnvReader
import okhttp3.OkHttpClient

class CUrlHttpHandler : HttpHandler {
    override fun isApplicable(type: HttpHandlerType): Boolean = type == HttpHandlerType.CURL

    override fun execute(
        project: Project,
        content: String,
        variablesName: Array<String>,
        variableTable: MutableMap<String, Any?>,
    ): String? {
        val processVariables: Map<String, String> = variablesName.associateWith { (variableTable[it] as? String ?: "") }

        val client = OkHttpClient()
        val request = runReadAction {
            val scope = getSearchScope(project)

            val envName = ShireEnvReader.getAllEnvironments(project, scope).firstOrNull() ?: ShireEnvReader.DEFAULT_ENV_NAME
            val envObject = ShireEnvReader.getEnvObject(envName, scope, project)

            val enVariables: List<Set<String>> = ShireEnvReader.fetchEnvironmentVariables(envName, scope)
            CUrlConverter.convert(content, enVariables, processVariables, envObject)
        }

        val response = client.newCall(request).execute()
        return response.body?.string()
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
