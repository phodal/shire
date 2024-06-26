package com.phodal.shirecore.middleware.builtin

import com.intellij.openapi.project.Project
import com.phodal.shirecore.markdown.Code
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor

class ParseCodeProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.ParseCode.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean {
        return true
    }

    override fun execute(project: Project, context: PostCodeHandleContext): String {
        return Code.parse(context.genText ?: "").text
    }
}
