package com.phodal.shirecore.toolchain.terminal

import com.intellij.openapi.project.Project

class TerminalHandler(
    val data: String,
    val project: Project,
    val onChunk: (str: String) -> Any?,
    val onDone: ((str: String) -> Any?)?,
)