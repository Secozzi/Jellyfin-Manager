package xyz.secozzi.jellyfinmanager.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

actual val isMaterialYouAvailable: Boolean = false

actual val MaterialYouColorScheme: @Composable (Boolean) -> ColorScheme = {
    if (it) lightColorScheme else darkColorScheme
}
