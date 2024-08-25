package com.phodal.shire.httpclient.handler

import com.intellij.openapi.project.Project
import com.phodal.shire.httpclient.converter.CUrlConverter
import okhttp3.OkHttpClient

class CUrlHttpHandler : HttpHandler {
    override fun execute(project: Project, content: String): String? {
        val client = OkHttpClient()
        val request = CUrlConverter.convert(project, content)
        val response = client.newCall(request).execute()

        return response.body?.string()
    }
}
