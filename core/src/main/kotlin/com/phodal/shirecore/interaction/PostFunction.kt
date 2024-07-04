package com.phodal.shirecore.interaction

import com.intellij.openapi.util.TextRange

typealias PostFunction = (response: String?, textRange: TextRange?) -> Unit