package com.phodal.shirelang.run.runner

import java.util.*

/**
 * SystemInfoVariable is a class that provides a way to resolve system information variables.
 * like:
 * - OS, OS Version..
 * - IDE information, version, etc.
 * - Timezone, locale, etc.
 */
class SystemInfoVariable {
    companion object {
        fun getOSInfo(): Map<String, Any> {
            val osInfo = mutableMapOf<String, Any>()
            osInfo["name"] = System.getProperty("os.name")
            osInfo["version"] = System.getProperty("os.version")
            return osInfo
        }

        fun getIDEInfo(): Map<String, Any> {
            val ideInfo = mutableMapOf<String, Any>()
            // Replace these with actual methods to get IDE information
            ideInfo["name"] = "IntelliJ IDEA"
            ideInfo["version"] = "2024.1"
            return ideInfo
        }

        fun getTimezoneInfo(): Map<String, Any> {
            val timezoneInfo = mutableMapOf<String, Any>()
            timezoneInfo["timezone"] = TimeZone.getDefault().displayName
            return timezoneInfo
        }

        fun getLocaleInfo(): Map<String, Any> {
            val localeInfo = mutableMapOf<String, Any>()
            localeInfo["locale"] = Locale.getDefault().toString()
            return localeInfo
        }

        fun resolve(): Map<out String, Any> {
            val results = mutableMapOf<String, Any>()
            results.putAll(getOSInfo())
            results.putAll(getIDEInfo())
            results.putAll(getTimezoneInfo())
            results.putAll(getLocaleInfo())
            return results
        }
    }
}
