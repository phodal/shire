package com.phodal.shirecore.ui.viewer

import com.intellij.openapi.Disposable

interface SketchViewer: Disposable {
    fun getViewText(): String
    fun updateViewText(text: String)
}