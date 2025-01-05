package com.phodal.shirelang.compiler.template

import com.intellij.openapi.util.UserDataHolderBase

/**
 * Variable Snapshot will store all change flow of a variable. For example:
 * ```shire
 * ---
 * variables:
 *   "controllers": /.*.java/ { cat | grep("class\s+([a-zA-Z]*Controller)")  }
 * ---
 * ```
 *
 * The variable snapshot should store:
 *
 * - the value after cat function
 * - the value after grep function
 */
class ResolvableVariableSnapshot(
    val variableName: String,
) : UserDataHolderBase() {

}


class ResolvableVariableSnapshotManager {

}