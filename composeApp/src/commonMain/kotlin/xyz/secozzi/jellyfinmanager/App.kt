package xyz.secozzi.jellyfinmanager

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.jetbrains.compose.ui.tooling.preview.Preview
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreen
import xyz.secozzi.jellyfinmanager.presentation.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        Navigator(screen = HomeScreen) { SlideTransition(it) }
    }
}
