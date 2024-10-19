package com.phodal.shirecore.function.guard.model

import kotlinx.serialization.Serializable

@Serializable
data class SecretPatterns(
    val patterns: List<SecretPatternItem>,
    val keywords: List<String> = emptyList(),
    val models: List<Model> = emptyList()
)

@Serializable
data class SecretPatternItem(
    val pattern: SecretPattern,
)

@Serializable
data class Model(
    val name: String,
    val location: String
)
