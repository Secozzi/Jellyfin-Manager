package xyz.secozzi.jellyfinmanager

import java.io.File

fun getConfigDir(): String {
    val appName = BuildKonfig.NAME.lowercase().replace(" ", "-")
    val userHome = System.getProperty("user.home")

    val appDataDir = when {
        System.getProperty("os.name").contains("windows", ignoreCase = true) -> {
            System.getenv("APPDATA")?.let { File(it, appName) } ?: File(userHome, "AppData/Roaming/$appName")
        }
        System.getProperty("os.name").contains("mac", ignoreCase = true) -> {
            File(userHome, "Library/Application Support/$appName")
        }
        else -> {
            File(userHome, ".config/$appName")
        }
    }

    appDataDir.mkdirs()
    return appDataDir.path
}
