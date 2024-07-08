package com.phodal.shirecore.search.function

enum class SemanticStorageType(val value: String) {
    MEMORY("memory"),
    DISK("disk"),
    ;

    companion object {
        fun fromString(value: String): SemanticStorageType {
            return values().firstOrNull { it.value == value }
                ?: MEMORY
        }
    }
}