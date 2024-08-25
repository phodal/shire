package com.phodal.shire.httpclient.converter

import com.intellij.httpClient.converters.curl.parser.CurlParser
import com.intellij.httpClient.execution.RestClientRequest
import com.intellij.httpClient.http.request.*
import com.intellij.ide.scratch.ScratchFileService
import com.intellij.ide.scratch.ScratchRootType
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.ui.UIBundle
import com.intellij.util.PathUtil
import java.io.IOException

object CUrlConverter {
    fun convert(myProject: Project, content: String): RestClientRequest {
        val restClientRequest = CurlParser().parseToRestClientRequest(content)
//
//        OpenInScratchFileUtil.createAndOpenScratchFile(
//            myProject,
//            restClientRequest,
//            Utils.createCurlStringComment(content)
//        )

        return restClientRequest
    }

    fun createAndOpenScratchFile(project: Project, request: RestClientRequest, comment: String?) {
        val fileName = PathUtil.makeFileName("rest-api", HttpRequestFileType.INSTANCE.defaultExtension)
        try {
            WriteCommandAction.writeCommandAction(project).withName("Create HTTP Request scratch file")
                .withGlobalUndo()
                .shouldRecordActionForActiveDocument(false)
                .withUndoConfirmationPolicy(UndoConfirmationPolicy.REQUEST_CONFIRMATION)
                .compute<NavigatablePsiElement, Exception> {
                    val convertedRequest: String
                    val fileService = ScratchFileService.getInstance()
                    try {
                        val file = fileService.findFile(
                            ScratchRootType.getInstance(),
                            fileName,
                            ScratchFileService.Option.create_new_always
                        )
                        fileService.scratchesMapping.setMapping(file, HttpRequestLanguage.INSTANCE)
                        val psiFile = PsiManager.getInstance(project).findFile(file) as? HttpRequestPsiFile
                            ?: throw Exception("Failed to create HTTP Request scratch file")

                        val manager = PsiDocumentManager.getInstance(project)
                        val document = manager.getDocument(psiFile)
                            ?: throw Exception("Created HTTP Request scratch file is invalid")

                        convertedRequest = if (comment != null) {
                            comment + HttpRequestPsiConverter.toPsiHttpRequest(request)
                        } else {
                            HttpRequestPsiConverter.toPsiHttpRequest(request)
                        }

                        document.insertString(document.textLength, convertedRequest)
                        manager.commitDocument(document)
                        val updated = HttpRequestPsiUtils.getRequestBlocks(psiFile)
                        if (updated.isNotEmpty()) {
                            return@compute updated[updated.size - 1]
                        }

                        return@compute psiFile
                    } catch (e: IOException) {
                        throw Exception("Could not create file: $e.")
                    }
                }?.navigate(true)
        } catch (e: Exception) {
            Messages.showErrorDialog(project, e.message, UIBundle.message("error.dialog.title", *arrayOfNulls(0)))
        }
    }
}