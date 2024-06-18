package com.phodal.shirelang.run.runner

import com.intellij.util.PlatformUtils
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
        fun fetchOsData(): Map<String, Any> {
            return mapOf(
                "name" to System.getProperty("os.name"),
                "version" to System.getProperty("os.version"),
                "arch" to System.getProperty("os.arch")
            )
        }

        fun fetchIDEData(): Map<String, Any> {
            val ideInfo = mutableMapOf<String, Any>()
            // Replace these with actual methods to get IDE information
            ideInfo["name"] = System.getProperty(PlatformUtils.PLATFORM_PREFIX_KEY, "idea")
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
            results.putAll(mapOf("os" to fetchOsData()))
            results.putAll(mapOf("ide" to fetchIDEData()))
            results.putAll(mapOf("timezone" to getTimezoneInfo()))
            results.putAll(mapOf("locale" to getLocaleInfo()))
            return results
        }
    }
}
