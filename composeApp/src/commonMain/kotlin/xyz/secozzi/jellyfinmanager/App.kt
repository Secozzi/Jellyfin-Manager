package xyz.secozzi.jellyfinmanager

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreen

@Composable
@Preview
fun App() {
    val preferences by localDI().instance<BasePreferences>()

    MaterialTheme {
        Navigator(
            screen = HomeScreen
        )
    }
}
