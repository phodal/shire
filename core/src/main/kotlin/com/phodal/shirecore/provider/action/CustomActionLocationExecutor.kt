package com.phodal.shirecore.provider.action

import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.llm.LlmProvider

interface CustomActionLocationExecutor {
    fun isApplicable(location: ShireActionLocation): Boolean

    fun execute(llmProvider: LlmProvider, location: ShireActionLocation)
}


