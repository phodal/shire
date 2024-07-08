package com.phodal.shirecore.search.function

import com.intellij.openapi.vfs.VirtualFile

data class IndexEntry(
    var index: Int,
    var count: Int,
    var chunk: String,
    val file: VirtualFile? = null,
    var embedding: FloatArray? = null,
)