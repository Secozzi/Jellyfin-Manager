package xyz.secozzi.jellyfinmanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.GlobalContext.startKoin
import xyz.secozzi.jellyfinmanager.di.initKoin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Jellyfin Manager",
    ) {
        startKoin {
            modules(
                initKoin(
                    datastorePath = getConfigDir("jellyfin-manager"),
                ),
            )
        }

        App()
    }
}
