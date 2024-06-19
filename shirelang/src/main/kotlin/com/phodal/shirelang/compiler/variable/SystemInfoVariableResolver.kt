package com.phodal.shirelang.compiler.variable

import com.intellij.openapi.application.ApplicationInfo
import java.util.*

/**
 * SystemInfoVariable is a class that provides a way to resolve system information variables.
 * like:
 * - OS, OS Version..
 * - IDE information, version, etc.
 * - Timezone, locale, etc.
 */
class SystemInfoVariableResolver(
    private val context: VariableResolverContext
) : VariableResolver {
    override fun resolve(): Map<String, Any> {
        return mapOf(
            "os" to fetchOsData(),
            "ide" to fetchIDEData(),
            "time" to fetchTimezoneData(),
            "locale" to fetchLocaleData()
        )
    }

    private fun fetchOsData(): Map<String, Any> {
        return mapOf(
            "name" to System.getProperty("os.name"),
            "version" to System.getProperty("os.version"),
            "arch" to System.getProperty("os.arch")
        )
    }

    /**
     * Get data from [com.intellij.util.PlatformUtils]
     */
    private fun fetchIDEData(): Map<String, Any> {
        val buildNumber = ApplicationInfo.getInstance().build
        return mapOf(
            "name" to System.getProperty("idea.platform.prefix", "idea"),
            "version" to buildNumber.asString(),
            "code" to buildNumber.productCode
        )
    }

    private fun fetchTimezoneData(): Map<String, Any> {
        return mapOf(
            "timezone" to TimeZone.getDefault().displayName,
            "date" to Calendar.getInstance().time,
            "today" to Calendar.getInstance().time,
            "now" to System.currentTimeMillis()
        )
    }

    private fun fetchLocaleData(): Map<String, Any> {
        return mapOf("locale" to Locale.getDefault().toString())
    }
}
