package com.phodal.shirelang

import com.intellij.DynamicBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey


@NonNls
private const val ShireBUNDLE: String = "messages.ShireBundle"

object ShireBundle : DynamicBundle(ShireBUNDLE) {
    @Suppress("SpreadOperator")
    @JvmStatic
    fun message(@PropertyKey(resourceBundle = ShireBUNDLE) key: String, vararg params: Any) = getMessage(key, *params)

    @Suppress("SpreadOperator", "unused")
    @JvmStatic
    fun messagePointer(@PropertyKey(resourceBundle = ShireBUNDLE) key: String, vararg params: Any) =
        getLazyMessage(key, *params)
}