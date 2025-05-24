package xyz.secozzi.jellyfinmanager

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import com.dokar.sonner.rememberToasterState
import xyz.secozzi.jellyfinmanager.presentation.theme.AppTheme
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.ui.home.HomeRoute
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreen
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryRoute
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryScreen
import xyz.secozzi.jellyfinmanager.ui.jellyfin.search.JellyfinSearchScreen
import xyz.secozzi.jellyfinmanager.ui.jellyfin.search.SearchRoute
import xyz.secozzi.jellyfinmanager.ui.preferences.AppearancePreferencesRoute
import xyz.secozzi.jellyfinmanager.ui.preferences.AppearancePreferencesScreen
import xyz.secozzi.jellyfinmanager.ui.preferences.PreferencesRoute
import xyz.secozzi.jellyfinmanager.ui.preferences.PreferencesScreen
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.ServerListRoute
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.ServerListScreen
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server.ServerRoute
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server.ServerScreen
import xyz.secozzi.jellyfinmanager.ui.providers.LocalToaster

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }

    AppTheme {
        Surface { Navigator() }
    }
}

@Composable
fun Navigator() {
    val toastState = rememberToasterState()
    CompositionLocalProvider(
        LocalNavController provides rememberNavController(),
        LocalToaster provides toastState,
    ) {
        NavHost(
            navController = LocalNavController.current,
            startDestination = HomeRoute,
            enterTransition = {
                fadeIn(animationSpec = tween(220)) +
                    slideIn(animationSpec = tween(220)) { IntOffset(it.width / 2, 0) }
            },
            exitTransition = {
                fadeOut(animationSpec = tween(220)) +
                    slideOut(animationSpec = tween(220)) { IntOffset(-it.width / 2, 0) }
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(220)) +
                    scaleIn(
                        animationSpec = tween(220, delayMillis = 30),
                        initialScale = .9f,
                        transformOrigin = TransformOrigin(-1f, .5f),
                    )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(220)) +
                    scaleOut(
                        animationSpec = tween(220, delayMillis = 30),
                        targetScale = .9f,
                        transformOrigin = TransformOrigin(-1f, .5f),
                    )
            },
        ) {
            composable<HomeRoute> { HomeScreen() }

            composable<PreferencesRoute> { PreferencesScreen() }
            composable<AppearancePreferencesRoute> { AppearancePreferencesScreen() }
            composable<ServerListRoute> { ServerListScreen() }
            composable<ServerRoute> { ServerScreen(it.toRoute<ServerRoute>().id) }

            composable<JellyfinEntryRoute>(typeMap = JellyfinEntryRoute.typeMap) {
                val data = it.toRoute<JellyfinEntryRoute>().data
                JellyfinEntryScreen(it, data.itemType, data.itemId)
            }
            composable<SearchRoute>(typeMap = SearchRoute.typeMap) {
                val searchQuery = it.toRoute<SearchRoute>().data.searchQuery
                JellyfinSearchScreen(searchQuery)
            }
        }
    }
}
