package xyz.secozzi.jellyfinmanager

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import xyz.secozzi.jellyfinmanager.preferences.preference.collectAsState
import xyz.secozzi.jellyfinmanager.presentation.theme.DarkMode
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreenViewModel
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val preferences by inject<BasePreferences>()
    private val homeViewModel by viewModel<HomeScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delaySplashScreen()

        setContent {
            val dark by preferences.darkMode.collectAsState()
            val isSystemInDarkTheme = isSystemInDarkTheme()

            enableEdgeToEdge(
                SystemBarStyle.auto(
                    lightScrim = Color.White.toArgb(),
                    darkScrim = Color.White.toArgb(),
                ) { dark == DarkMode.Dark || (dark == DarkMode.System && isSystemInDarkTheme) },
            )

            App()
        }
    }

    private fun delaySplashScreen() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (homeViewModel.isLoaded.value) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            },
        )
    }
}
