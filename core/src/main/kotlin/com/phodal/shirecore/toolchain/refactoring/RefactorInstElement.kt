package com.phodal.shirecore.toolchain.refactoring

data class RefactorInstElement(
    val isClass: Boolean,
    val isMethod: Boolean,
    val methodName: String,
    val canonicalName: String,
    val className: String,
    val pkgName: String
)