package xyz.secozzi.jellyfinmanager.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreenContent
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import xyz.secozzi.jellyfinmanager.presentation.utils.Screen
import xyz.secozzi.jellyfinmanager.presentation.utils.Tab
import xyz.secozzi.jellyfinmanager.ui.home.components.DropDown
import xyz.secozzi.jellyfinmanager.ui.home.components.NoServerContent
import xyz.secozzi.jellyfinmanager.ui.home.tabs.jellyfin.JellyfinTab
import xyz.secozzi.jellyfinmanager.ui.home.tabs.ssh.SSHTab
import xyz.secozzi.jellyfinmanager.ui.preferences.PreferencesScreen
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server.ServerScreen

object HomeScreen : Screen() {
    private fun readResolve(): Any = HomeScreen

    private const val TAB_FADE_DURATION = 200
    private const val TAB_NAVIGATOR_KEY = "HomeTabs"

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = koinScreenModel<HomeScreenScreenModel>()
        val serverList by screenModel.serverList.collectAsState()
        val state by screenModel.state.collectAsState()

        val tabs = persistentListOf(
            SSHTab,
            JellyfinTab,
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        state.getSuccessDataOrNull()?.let { selected ->
                            DropDown(
                                server = selected,
                                values = serverList.toPersistentList(),
                                onSelect = screenModel::selectServer,
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { navigator.push(PreferencesScreen) }) {
                            Icon(Icons.Rounded.Settings, null)
                        }
                    }
                )
            },
            contentWindowInsets = WindowInsets(0),
        ) { contentPadding ->
            ServerContent(
                state = state,
                serverList = serverList,
                tabs = tabs,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }

    @Composable
    private fun ServerContent(
        state: RequestState<Server?>,
        serverList: List<Server>,
        tabs: ImmutableList<Tab>,
        modifier: Modifier = Modifier,
    ) {
        val navigator = LocalNavigator.currentOrThrow

        TabNavigator(
            tab = tabs.first(),
            key = TAB_NAVIGATOR_KEY,
        ) { tabNavigator ->
            Scaffold(
                modifier = modifier,
                bottomBar = {
                    NavigationBar {
                        tabs.fastForEach {
                            NavigationBarItem(it)
                        }
                    }
                },
                contentWindowInsets = WindowInsets(0),
            ) { contentPadding ->
                Box(
                    modifier = Modifier
                        .padding(contentPadding)
                        .consumeWindowInsets(contentPadding),
                ) {
                    AnimatedContent(
                        targetState = tabNavigator.current,
                        transitionSpec = {
                            materialFadeThroughIn(
                                initialScale = 1f,
                                durationMillis = TAB_FADE_DURATION,
                            ) togetherWith
                                    materialFadeThroughOut(durationMillis = TAB_FADE_DURATION)
                        },
                        label = "tabContent",
                    ) {
                        tabNavigator.saveableState(key = "currentTab", it) {
                            if (state.isIdle() || state.isLoading()) {
                                LoadingScreenContent()
                                return@saveableState
                            }

                            if (serverList.isEmpty()) {
                                NoServerContent(
                                    onClick = { navigator.push(ServerScreen(null)) },
                                )
                                return@saveableState
                            }

                            it.Content()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.NavigationBarItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val selected = tabNavigator.current::class == tab::class

        NavigationBarItem(
            selected = selected,
            onClick = {
                if (!selected) {
                    tabNavigator.current = tab
                } else {
                    scope.launch { tab.onReselect(navigator) }
                }
            },
            icon = { NavigationIconItem(tab) },
            label = {
                Text(
                    text = tab.options.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            alwaysShowLabel = true,
        )
    }

    @Composable
    private fun NavigationIconItem(tab: Tab) {
        Box {
            Icon(
                painter = tab.options.icon!!,
                contentDescription = tab.options.title,
                // TODO: https://issuetracker.google.com/u/0/issues/316327367
                tint = LocalContentColor.current,
            )
        }
    }
}
