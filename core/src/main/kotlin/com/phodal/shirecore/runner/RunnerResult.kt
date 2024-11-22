// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.phodal.shirecore.runner

import org.jetbrains.annotations.Nls

class RunnerResult(
    val status: RunnerStatus,
    @Nls(capitalization = Nls.Capitalization.Sentence) val message: String = "",
    val details: String? = null,
    val diff: CheckResultDiff? = null,
) {

    companion object {
        val noTestsRun: RunnerResult
            get() = RunnerResult(
                RunnerStatus.Unchecked,
                "check.no.tests.with.help.guide",
            )
    }

}

data class CheckResultDiff(val expected: String, val actual: String, val title: String = "")
