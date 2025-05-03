package xyz.secozzi.jellyfinmanager

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreen
import xyz.secozzi.jellyfinmanager.presentation.theme.AppTheme

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }

    AppTheme {
        Navigator(screen = HomeScreen) { SlideTransition(it) }
    }
}
