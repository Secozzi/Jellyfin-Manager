package xyz.secozzi.jellyfinmanager

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreen
import xyz.secozzi.jellyfinmanager.presentation.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        Navigator(screen = HomeScreen) { SlideTransition(it) }
    }
}
