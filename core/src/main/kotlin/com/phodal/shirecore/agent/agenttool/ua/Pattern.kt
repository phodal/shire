/**
 * This is free and unencumbered software released into the public domain.
 * GitHub: https://github.com/jonaskahn/user-agents
 */
package com.phodal.shirecore.agent.agenttool.ua

object Pattern {
    const val WINDOWS_CHROME_AGENT = "Mozilla/5.0 (%s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%s Safari/537.36"
    const val WINDOWS_FIREFOX_AGENT = "Mozilla/5.0 (%s; rv:%s) Gecko/20100101 Firefox/%s"

    const val LINUX_CHROME_AGENT = WINDOWS_CHROME_AGENT
    const val LINUX_FIREFOX_AGENT = WINDOWS_FIREFOX_AGENT

    const val MACOS_FIREFOX_AGENT = WINDOWS_FIREFOX_AGENT
    const val MACOS_CHROME_AGENT = WINDOWS_CHROME_AGENT
    const val MACOS_SAFARI_AGENT = "Mozilla/5.0 (%s) AppleWebKit/%s (KHTML, like Gecko) Version/%s Safari/%s"

    const val IOS_FIREFOX_AGENT =
        "Mozilla/5.0 (%s) AppleWebKit/%s (KHTML, like Gecko) FxiOS/%s Mobile/%s Safari/%s"
    const val IOS_CHROME_AGENT =
        "Mozilla/5.0 (%s) AppleWebKit/%s (KHTML, like Gecko) CriOS/%s Mobile/%s Safari/%s"
    const val IOS_SAFARI_AGENT =
        "Mozilla/5.0 (%s) AppleWebKit/%s (KHTML, like Gecko) Version/%s Mobile/%s Safari/%s"

    const val ANDROID_FIREFOX_AGENT =
        "Mozilla/5.0 (%s; rv:%s) Gecko/%s Firefox/%s"
    const val ANDROID_CHROME_AGENT =
        "Mozilla/5.0 (%s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%s Mobile Safari/537.36"
}