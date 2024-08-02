package com.phodal.shirecore.guard.scanner

import com.intellij.openapi.components.Service
import com.phodal.shirecore.guard.base.LocalScanner
import com.phodal.shirecore.guard.base.Masker
import com.phodal.shirecore.guard.base.ScanResult

@Service(Service.Level.PROJECT)
class BanKeywordsScanner : LocalScanner, Masker {
    /**
     * Todo load from `shireCustomGuardingRules.yaml`
     */
    override fun scan(prompt: String): ScanResult {
        TODO("Not yet implemented")
    }

    override fun mask(prompt: String): String {
        TODO("Not yet implemented")
    }
}
