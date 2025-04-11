package xyz.secozzi.jellyfinmanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
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
        Window(
            onCloseRequest = ::exitApplication,
            title = BuildKonfig.NAME,
        ) {
            windowBackgroundFlashingOnCloseFixHack()
            App()
        }
    }
}
