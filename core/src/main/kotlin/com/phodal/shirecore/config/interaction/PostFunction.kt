package com.phodal.shirecore.config.interaction

import com.intellij.openapi.util.TextRange

/**
 * Don't remove public modifier, it's required Kotlin compile, in IDEA will failed.
 */
public typealias PostFunction = (response: String?, textRange: TextRange?) -> Unit
