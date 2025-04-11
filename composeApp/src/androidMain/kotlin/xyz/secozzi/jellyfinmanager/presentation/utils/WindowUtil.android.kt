package xyz.secozzi.jellyfinmanager.presentation.utils

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
actual fun isLandscapeMode(): Boolean {
    val conf = LocalConfiguration.current
    return conf.orientation == Configuration.ORIENTATION_LANDSCAPE
}
