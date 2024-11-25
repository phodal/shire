package com.phodal.shirecore.variable.frontend

interface ComponentProvider {
    fun getPages(): List<Component>
    fun getComponents(): List<Component>
    fun getRoutes(): Map<String, String>
}
