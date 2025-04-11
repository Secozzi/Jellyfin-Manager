package xyz.secozzi.jellyfinmanager.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun isLandscapeMode(): Boolean {
    val windowSize = LocalWindowInfo.current.containerSize
    return windowSize.width > windowSize.height * 1.25
}
