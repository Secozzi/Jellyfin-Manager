package xyz.secozzi.jellyfinmanager

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.koin.core.context.GlobalContext.startKoin
import xyz.secozzi.jellyfinmanager.di.initKoin
import xyz.secozzi.jellyfinmanager.presentation.utils.windowBackgroundFlashingOnCloseFixHack

fun main() {
    startKoin {
        modules(
            initKoin(
                datastorePath = getConfigDir(),
            ),
        )
    }

    application {
        val windowState = rememberWindowState(
            width = 1366.dp,
            height = 800.dp,
        )

        Window(
            onCloseRequest = ::exitApplication,
            title = BuildKonfig.NAME,
            state = windowState,
        ) {
            windowBackgroundFlashingOnCloseFixHack()
            App()
        }
    }
}
