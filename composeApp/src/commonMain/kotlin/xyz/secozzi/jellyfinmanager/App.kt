package xyz.secozzi.jellyfinmanager

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreen

@Composable
fun App() {
    MaterialTheme {
        Navigator(
            screen = HomeScreen
        )
    }
}
