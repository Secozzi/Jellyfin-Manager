package xyz.secozzi.jellyfinmanager.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import jellyfin_manager.composeapp.generated.resources.Res
import jellyfin_manager.composeapp.generated.resources.jellyfin
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreen
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.presentation.utils.UiState
import xyz.secozzi.jellyfinmanager.ui.home.components.DropDown
import xyz.secozzi.jellyfinmanager.ui.home.components.NoServerContent
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinRoute
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinScreen
import xyz.secozzi.jellyfinmanager.ui.preferences.PreferencesRoute
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server.ServerRoute
import xyz.secozzi.jellyfinmanager.ui.ssh.SSHRoute
import xyz.secozzi.jellyfinmanager.ui.ssh.SSHScreen

@Serializable
data object HomeRoute

data class BottomBarRoute<out T>(val name: String, val route: T, val icon: ImageVector)

@Composable
fun HomeScreen() {
    val tabNavigator = rememberNavController()
    val navigator = LocalNavController.current

    val viewModel = koinViewModel<HomeScreenViewModel>()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val servers by viewModel.servers.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val tabs = persistentListOf(
        BottomBarRoute("Jellyfin", JellyfinRoute, vectorResource(Res.drawable.jellyfin)),
        BottomBarRoute("SSH", SSHRoute, Icons.Default.Terminal),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    servers.takeIf { uiState.isSuccess() && servers.isNotEmpty() }?.let { serverList ->
                        DropDown(
                            server = selectedServer!!,
                            values = serverList.toPersistentList(),
                            onSelect = viewModel::selectServer,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navigator.navigate(PreferencesRoute) }) {
                        Icon(Icons.Rounded.Settings, null)
                    }
                },
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.isSuccess(),
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(300)),
            ) {
                NavigationBar(
                    containerColor = NavigationBarDefaults.containerColor.copy(alpha = 0.34f),
                    windowInsets = WindowInsets.navigationBars,
                ) {
                    val navBackStackEntry by tabNavigator.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    tabs.forEach { bottomRoute ->
                        NavigationBarItem(
                            icon = { Icon(bottomRoute.icon, contentDescription = bottomRoute.name) },
                            label = { Text(bottomRoute.name) },
                            selected = currentDestination
                                ?.hierarchy
                                ?.any { it.hasRoute(bottomRoute.route::class) } == true,
                            onClick = {
                                tabNavigator.navigate(bottomRoute.route) {
                                    popUpTo(tabNavigator.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0),
    ) { contentPadding ->
        HomeScreenContent(
            tabNavigator = tabNavigator,
            uiState = uiState,
            paddingValues = contentPadding,
        )
    }
}

@Composable
private fun HomeScreenContent(
    tabNavigator: NavHostController,
    uiState: UiState<Unit>,
    paddingValues: PaddingValues,
) {
    val navigator = LocalNavController.current

    Surface(modifier = Modifier.padding(paddingValues)) {
        when {
            uiState.isWaiting() -> {
                LoadingScreen()
            }
            uiState.isError() -> {
                NoServerContent(
                    onClick = { navigator.navigate(ServerRoute(null)) },
                )
            }
            else -> {
                NavHost(
                    navController = tabNavigator,
                    startDestination = JellyfinRoute,
                ) {
                    composable<JellyfinRoute> { JellyfinScreen() }
                    composable<SSHRoute> { SSHScreen() }
                }
            }
        }
    }
}
