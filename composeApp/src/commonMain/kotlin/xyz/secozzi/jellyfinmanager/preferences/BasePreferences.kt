package xyz.secozzi.jellyfinmanager.preferences

import xyz.secozzi.jellyfinmanager.preferences.preference.PreferenceStore
import xyz.secozzi.jellyfinmanager.preferences.preference.getEnum
import xyz.secozzi.jellyfinmanager.presentation.theme.DarkMode

class BasePreferences(
    preferences: PreferenceStore
) {
    // Appearance
    val darkMode = preferences.getEnum("dark_mode", DarkMode.System)
    val materialYou = preferences.getBoolean("material_you")

    // SSH
    val address = preferences.getString("address", "127.0.0.1")
    val port = preferences.getInt("port", 22)
    val hostname = preferences.getString("hostname", "")
    val password = preferences.getString("password", "")
    val baseDir = preferences.getString("base_dir", "/home")
    val dirBlacklist = preferences.getString("dir_blacklist", "")
}
