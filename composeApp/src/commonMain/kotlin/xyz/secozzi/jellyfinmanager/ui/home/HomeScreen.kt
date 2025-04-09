package xyz.secozzi.jellyfinmanager.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import kotlinx.coroutines.launch
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut
import xyz.secozzi.jellyfinmanager.presentation.utils.Screen
import xyz.secozzi.jellyfinmanager.presentation.utils.Tab
import xyz.secozzi.jellyfinmanager.ui.home.tabs.jellyfin.JellyfinTab
import xyz.secozzi.jellyfinmanager.ui.home.tabs.ssh.SSHTab
import xyz.secozzi.jellyfinmanager.ui.preferences.PreferencesScreen

object HomeScreen : Screen() {
    private fun readResolve(): Any = HomeScreen

    private const val TAB_FADE_DURATION = 200
    private const val TAB_NAVIGATOR_KEY = "HomeTabs"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val tabs = listOf(
            SSHTab,
            JellyfinTab,
        )

        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        OutlinedButton(onClick = {}) {
                            Text("Select server")
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
            TabNavigator(
                tab = tabs.first(),
                key = TAB_NAVIGATOR_KEY,
            ) { tabNavigator ->
                Scaffold(
                    modifier = Modifier.padding(contentPadding),
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
                                it.Content()
                            }
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