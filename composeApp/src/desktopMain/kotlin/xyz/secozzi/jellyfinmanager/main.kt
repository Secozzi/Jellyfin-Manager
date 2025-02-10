package xyz.secozzi.jellyfinmanager

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mayakapps.compose.windowstyler.WindowBackdrop
import com.mayakapps.compose.windowstyler.WindowStyle
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext.startKoin
import xyz.secozzi.jellyfinmanager.di.initKoin
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import xyz.secozzi.jellyfinmanager.preferences.preference.collectAsState
import xyz.secozzi.jellyfinmanager.presentation.theme.DarkMode

fun main() {
    startKoin {
        modules(
            initKoin(
                datastorePath = getConfigDir("jellyfin-manager"),
            ),
        )
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Jellyfin Manager",
        ) {
            val preferences = koinInject<BasePreferences>()
            val darkMode by preferences.darkMode.collectAsState()

            val isDarkTheme = when (darkMode) {
                DarkMode.Light -> false
                DarkMode.Dark -> true
                DarkMode.System -> isSystemInDarkTheme()
            }

            WindowStyle(
                isDarkTheme = isDarkTheme,
                backdropType = WindowBackdrop.Mica,
            )

            App()
        }
    }
}