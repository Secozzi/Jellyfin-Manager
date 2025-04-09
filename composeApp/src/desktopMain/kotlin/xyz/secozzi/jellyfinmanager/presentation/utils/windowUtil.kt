package xyz.secozzi.jellyfinmanager.presentation.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.window.WindowScope

/**
 * A hack to work around the window flashing its background color when closed
 * (https://github.com/JetBrains/compose-multiplatform/issues/3790).
 */
@Composable
fun WindowScope.windowBackgroundFlashingOnCloseFixHack() {
    val backgroundColor = MaterialTheme.colorScheme.background
    LaunchedEffect(window, backgroundColor) {
        window.background = java.awt.Color(backgroundColor.toArgb())
    }
}
