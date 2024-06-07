package com.phodal.shirelang.compiler.hobbit

sealed class SelectElementStrategy {
    /**
     * Selection element
     */
    abstract fun select()

    /**
     * Auto select parent block element, like function, class, etc.
     */
    object DEFAULT : SelectElementStrategy() {
        override fun select() {

        }
    }
}