package xyz.secozzi.jellyfinmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import org.koin.android.ext.android.inject
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import xyz.secozzi.jellyfinmanager.preferences.preference.collectAsState
import xyz.secozzi.jellyfinmanager.presentation.theme.DarkMode
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val preferences by inject<BasePreferences>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val dark by preferences.darkMode.collectAsState()
            val isSystemInDarkTheme = isSystemInDarkTheme()

            enableEdgeToEdge(
                SystemBarStyle.auto(
                    lightScrim = Color.White.toArgb(),
                    darkScrim = Color.White.toArgb()
                ) { dark == DarkMode.Dark || (dark == DarkMode.System && isSystemInDarkTheme) },
            )

            App()
        }
    }
}
