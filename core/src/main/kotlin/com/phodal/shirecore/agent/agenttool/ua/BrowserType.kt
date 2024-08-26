/**
 * This is free and unencumbered software released into the public domain.
 * GitHub: https://github.com/jonaskahn/user-agents
 */
package com.phodal.shirecore.agent.agenttool.ua

enum class BrowserType {
    CHROME,
    FIREFOX,
    SAFARI;

    companion object {
        fun all(): List<BrowserType> {
            return arrayListOf(CHROME, FIREFOX, SAFARI)
        }

        fun cross(): List<BrowserType> {
            return arrayListOf(CHROME, FIREFOX)
        }
    }
}