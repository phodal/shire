package com.phodal.shire.httpclient.handler

import com.intellij.openapi.project.Project

interface HttpHandler {
    fun execute(project: Project, content: String) : String?
}
