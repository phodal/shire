package com.phodal.shirecore.guard.model

data class SecretPatterns(
    var patterns: List<SecretPatternItem> = emptyList(),
    var keywords: List<String> = emptyList(),
    var models: List<Model> = emptyList(),
)

data class SecretPatternItem(
    var pattern: SecretPattern = SecretPattern(),
)

data class Model(
    var name: String = "",
    var location: String = "",
)
