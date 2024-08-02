package com.phodal.shirecore.guard.model

import kotlinx.serialization.Serializable

@Serializable
data class SecretPatterns(
    val patterns: List<SecretPatternItem>,
)

@Serializable
data class SecretPatternItem(
    val pattern: SecretPattern,
)

