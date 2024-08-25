package com.phodal.shire.httpclient.handler

import com.intellij.httpClient.http.request.HttpRequestCollectionProvider
import com.intellij.httpClient.http.request.notification.HttpClientWhatsNewContentService
import com.intellij.ide.scratch.ScratchUtil
import com.intellij.ide.scratch.ScratchesSearchScope
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopesCore
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.indexing.FileBasedIndex
import com.phodal.shire.httpclient.converter.CUrlConverter
import com.phodal.shirecore.index.SHIRE_ENV_ID
import com.phodal.shirecore.provider.http.HttpHandler
import com.phodal.shirecore.provider.http.HttpHandlerType
import okhttp3.OkHttpClient

class CUrlHttpHandler : HttpHandler {
    override fun isApplicable(type: HttpHandlerType): Boolean = type == HttpHandlerType.CURL

    override fun execute(project: Project, content: String): String? {
        val client = OkHttpClient()
        val request = CUrlConverter.convert(project, content)
        val response = client.newCall(request).execute()

//        fetchEnvironmentVariables(project)

        return response.body?.string()
    }

    private fun fetchEnvironmentVariables(project: Project): MutableList<Set<String>> {
        val firstEnv = getAllEnvironments(project, getSearchScope(project)).firstOrNull() ?: "development"
        val variables: MutableList<Set<String>> = FileBasedIndex.getInstance().getValues(
            SHIRE_ENV_ID,
            firstEnv,
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
