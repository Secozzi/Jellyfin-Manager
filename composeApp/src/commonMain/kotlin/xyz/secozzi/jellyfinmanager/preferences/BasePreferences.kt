package xyz.secozzi.jellyfinmanager.preferences

import xyz.secozzi.jellyfinmanager.preferences.preference.PreferenceStore
import xyz.secozzi.jellyfinmanager.preferences.preference.getEnum
import xyz.secozzi.jellyfinmanager.presentation.theme.DarkMode

class BasePreferences(
    preferences: PreferenceStore,
) {
    val sqlPassword = preferences.getString("sql_password")
    val darkMode = preferences.getEnum("dark_mode", DarkMode.Dark)
    val materialYou = preferences.getBoolean("material_you")
}
