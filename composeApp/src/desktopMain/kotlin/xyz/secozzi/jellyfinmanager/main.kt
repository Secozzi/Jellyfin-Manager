package xyz.secozzi.jellyfinmanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.kodein.di.compose.withDI
import xyz.secozzi.jellyfinmanager.di.initKodein

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Jellyfin Manager",
    ) {
        val filesDirPath = getConfigDir("jellyfin-manager")
        val di = initKodein(filesDirPath)

        withDI(di = di) {
            App()
        }
    }
}
