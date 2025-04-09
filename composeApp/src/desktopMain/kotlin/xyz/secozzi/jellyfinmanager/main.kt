package xyz.secozzi.jellyfinmanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = BuildKonfig.NAME,
    ) {
        App()
    }
}
