package com.phodal.shire.httpclient.handler

import com.intellij.httpClient.http.request.HttpRequestLanguage
import com.intellij.ide.scratch.ScratchRootType
import com.intellij.openapi.project.Project

class JbHttpHandler : HttpHandler {
    override fun execute(project: Project, content: String): String? {
        val scratchFile = ScratchRootType.getInstance()
            .createScratchFile(project, "shire.http", HttpRequestLanguage.INSTANCE, content)
            ?: return null

        return ""
    }
}