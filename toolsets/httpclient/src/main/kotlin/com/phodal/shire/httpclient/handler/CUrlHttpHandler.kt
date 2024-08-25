package com.phodal.shire.httpclient.handler

import com.intellij.openapi.project.Project
import com.intellij.psi.search.ProjectScope
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

        val variables = FileBasedIndex.getInstance().getValues<String, Set<String>>(
            SHIRE_ENV_ID,
            "development",
            ProjectScope.getContentScope(project)
        )

        return response.body?.string()
    }
}
