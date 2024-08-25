package com.phodal.shire.httpclient.handler

import com.intellij.openapi.project.Project
import com.phodal.shire.httpclient.converter.CUrlConverter
import com.phodal.shirecore.provider.http.HttpHandler
import com.phodal.shirecore.provider.http.HttpHandlerType
import okhttp3.OkHttpClient

class CUrlHttpHandler : HttpHandler {
    override fun isApplicable(type: HttpHandlerType): Boolean = type == HttpHandlerType.CURL

    override fun execute(project: Project, content: String): String? {
        val client = OkHttpClient()
        val request = CUrlConverter.convert(project, content)
        val response = client.newCall(request).execute()

        return response.body?.string()
    }
}
