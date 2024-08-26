/**
 * This is free and unencumbered software released into the public domain.
 * GitHub: https://github.com/jonaskahn/user-agents
 */
package com.phodal.shirecore.agent.agenttool.ua

import java.util.*
import java.util.concurrent.ThreadLocalRandom

object RandomUserAgent {

    private val random = ThreadLocalRandom.current()

    /**
     * Random a user-agent, result can be a {@link #desktop(DeviceType, BrowserType) desktop}
     * or {@link #mobile(DeviceType, BrowserType) mobile} user-agent type
     *
     */
    fun random(): String {
        return if (random.nextBoolean()) desktop() else mobile()
    }

    /**
     * Generate a desktop(MacOS, Linux, Windows) user-agent
     * @param deviceType DeviceType must be a valid type or null. If device type is mobile, an exception will be thrown.
     * @param browserType BrowserType must be a valid type or null.
     * @return {@link String}
     * @see one.ifelse.tools.useragent.types.DeviceType
     * @see one.ifelse.tools.useragent.types.BrowserType
     */
    fun desktop(
        deviceType: DeviceType? = null,
        browserType: BrowserType? = null
    ): String {
        val device = deviceType ?: DeviceType.desktop().random()
        return when (device) {
            DeviceType.MACOS -> generateMacOS(browserType)
            DeviceType.LINUX -> generateLinux(browserType)
            DeviceType.WINDOWS -> generateWindowsAgent(browserType)
            DeviceType.IOS -> throw UserAgentException("Require desktop device type like: ${DeviceType.desktop()}")
            DeviceType.ANDROID -> throw UserAgentException("Require desktop device type like: ${DeviceType.desktop()}")
        }
    }

    private fun generateWindowsAgent(browserType: BrowserType?): String {
        val windowsVersion = Seeds.WINDOWS_DEVICES.random()
        val type = browserType ?: BrowserType.cross().random()
        return when (type) {
            BrowserType.CHROME -> {
                String.format(
                    Pattern.WINDOWS_CHROME_AGENT,
                    windowsVersion,
                    Seeds.CHROME_VERSIONS.random()
                )
            }

            BrowserType.FIREFOX -> {
                val firefoxVersion = Seeds.FIREFOX_VERSIONS.random()
                String.format(
                    Pattern.WINDOWS_FIREFOX_AGENT,
                    windowsVersion,
                    firefoxVersion,
                    firefoxVersion
                )
            }

            BrowserType.SAFARI -> {
                throw UserAgentException("Cannot generate user-gent for Windows and Safari")
            }
        }
    }

    private fun generateLinux(browserType: BrowserType?): String {
        val linuxVersion = Seeds.LINUX_DEVICES.random()
        val type = browserType ?: BrowserType.cross().random()
        return when (type) {
            BrowserType.CHROME -> {
                String.format(
                    Pattern.LINUX_CHROME_AGENT,
                    linuxVersion,
                    Seeds.CHROME_VERSIONS.random()
                )
            }

            BrowserType.FIREFOX -> {
                val firefoxVersion = Seeds.FIREFOX_VERSIONS.random()
                return String.format(
                    Pattern.LINUX_FIREFOX_AGENT,
                    linuxVersion,
                    firefoxVersion,
                    firefoxVersion
                )
            }

            BrowserType.SAFARI -> {
                throw UserAgentException("Cannot generate user-gent for Linux and Safari")
            }
        }
    }

    private fun generateMacOS(browserType: BrowserType?): String {
        val type = browserType ?: BrowserType.all().random()
        val arch = Seeds.MAC_ARCHS.random()
        val version = Seeds.MAC_VERSIONS.random()
        val device = "$arch $version"
        return when (type) {
            BrowserType.CHROME -> {
                String.format(
                    Pattern.MACOS_CHROME_AGENT,
                    device,
                    Seeds.CHROME_VERSIONS.random(),
                )
            }

            BrowserType.FIREFOX -> {
                val firefoxVersion = Seeds.FIREFOX_VERSIONS.random()
                return String.format(
                    Pattern.MACOS_FIREFOX_AGENT,
                    device,
                    firefoxVersion,
                    firefoxVersion
                )
            }

            BrowserType.SAFARI -> {
                val safariVersion = Seeds.SAFARI_VERSIONS.random()
                return String.format(
                    Pattern.MACOS_SAFARI_AGENT,
                    device,
                    safariVersion,
                    version.replace("_", "."),
                    safariVersion
                )
            }
        }
    }


    /**
     * Generate a mobile(IOS, Android) user-agent
     * @param deviceType DeviceType must be a valid type or null. If device type is desktop, an exception will be thrown.
     * @param browserType BrowserType must be a valid type or null.
     * @return {@link String}
     * @see one.ifelse.tools.useragent.types.DeviceType
     * @see one.ifelse.tools.useragent.types.BrowserType
     */
    fun mobile(
        deviceType: DeviceType? = null,
        browserType: BrowserType? = null
    ): String {
        val device = deviceType ?: DeviceType.mobile().random()
        return when (device) {
            DeviceType.MACOS -> throw UserAgentException("Require mobile device type like: ${DeviceType.mobile()}")
            DeviceType.LINUX -> throw UserAgentException("Require mobile device type like: ${DeviceType.mobile()}")
            DeviceType.WINDOWS -> throw UserAgentException("Require mobile device type like: ${DeviceType.mobile()}")
            DeviceType.IOS -> generateIOSAgent(browserType)
            DeviceType.ANDROID -> generateAndroidAgent(browserType)
        }
    }

    private fun generateIOSAgent(browserType: BrowserType?): String {
        val type = browserType ?: BrowserType.all().random()
        val version = Seeds.IOS_VERSIONS.entries.random()
        val arch = Seeds.IOS_ARCHS.random()
        val device = String.format(arch, version.key)
        val safariVersion = Seeds.SAFARI_VERSIONS.random()
        return when (type) {
            BrowserType.CHROME -> {
                String.format(
                    Pattern.IOS_CHROME_AGENT,
                    device,
                    safariVersion,
                    Seeds.CHROME_VERSIONS.random(),
                    version.value,
                    safariVersion
                )
            }

            BrowserType.FIREFOX -> {
                String.format(
                    Pattern.IOS_FIREFOX_AGENT,
                    device,
                    safariVersion,
                    Seeds.FIREFOX_VERSIONS.random(),
                    version.value,
                    safariVersion
                )
            }

            BrowserType.SAFARI -> {
                String.format(
                    Pattern.IOS_SAFARI_AGENT,
                    device,
                    safariVersion,
                    safariVersion,
                    version.value,
                    safariVersion
                )
            }
        }
    }

    private fun generateAndroidAgent(browserType: BrowserType?): String {
        val type = browserType ?: BrowserType.cross().random()
        val device = Seeds.ANDROID_DEVICES.random()
        return when (type) {
            BrowserType.CHROME -> {
                val chromeVersion = Seeds.CHROME_VERSIONS.random()
                String.format(
                    Pattern.ANDROID_CHROME_AGENT,
                    device,
                    chromeVersion,
                )
            }

            BrowserType.FIREFOX -> {
                val firefoxVersion = Seeds.FIREFOX_VERSIONS.random()
                String.format(
                    Pattern.ANDROID_FIREFOX_AGENT,
                    device,
                    firefoxVersion,
                    firefoxVersion,
                    firefoxVersion
                )
            }

            BrowserType.SAFARI -> throw UserAgentException("Cannot generate user-gent for Android and Safari")
        }
    }
}
