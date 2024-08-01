package com.phodal.shirecore.guard.input

import com.intellij.openapi.components.Service
import com.phodal.shirecore.guard.LocalScanner
import com.phodal.shirecore.guard.Replacer
import com.phodal.shirecore.guard.ScanResult

@Service(Service.Level.PROJECT)
class BanKeywordsScanner : LocalScanner, Replacer {
    /**
     * Todo load from `shireCustomGuardingRules.yaml`
     */
    override fun scan(prompt: String): ScanResult {
        TODO("Not yet implemented")
    }

    override fun replace(prompt: String): String {
        TODO("Not yet implemented")
    }
}
