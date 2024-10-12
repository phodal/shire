package com.phodal.shire.mock.provider

enum class WiremockFunction(val funName: String) {
    Mock("mock")
    ;

    companion object {
        fun fromString(value: String): WiremockFunction? {
            return entries.firstOrNull { it.funName == value }
        }
    }
}