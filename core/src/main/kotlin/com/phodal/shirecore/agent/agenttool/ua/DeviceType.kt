/**
 * This is free and unencumbered software released into the public domain.
 * GitHub: https://github.com/jonaskahn/user-agents
 */
package com.phodal.shirecore.agent.agenttool.ua

enum class DeviceType {
    MACOS,
    LINUX,
    WINDOWS,
    IOS,
    ANDROID;

    companion object {
        fun mobile(): List<DeviceType> {
            return arrayListOf(IOS, ANDROID)
        }

        fun desktop(): List<DeviceType> {
            return arrayListOf(MACOS, LINUX, WINDOWS)
        }
    }
}