package com.phodal.shire.httpclient.converter

import com.intellij.httpClient.converters.curl.parser.CurlParser
import com.intellij.httpClient.execution.RestClientRequest
import com.intellij.openapi.project.Project

object CUrlConverter {
    fun convert(myProject: Project, content: String): RestClientRequest {
        val restClientRequest = CurlParser().parseToRestClientRequest(content)


        /// todo: convert to our request model

        return restClientRequest
    }
}