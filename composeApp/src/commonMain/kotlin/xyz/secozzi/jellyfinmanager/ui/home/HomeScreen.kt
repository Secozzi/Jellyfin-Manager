package xyz.secozzi.jellyfinmanager.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import kotlinx.coroutines.launch
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut
import xyz.secozzi.jellyfinmanager.presentation.Screen
import xyz.secozzi.jellyfinmanager.presentation.utils.Tab
import xyz.secozzi.jellyfinmanager.ui.home.tabs.JellyfinTab
import xyz.secozzi.jellyfinmanager.ui.home.tabs.SSHTab

object HomeScreen : Screen() {
    private fun readResolve(): Any = HomeScreen

    private const val TAB_FADE_DURATION = 200
    private const val TAB_NAVIGATOR_KEY = "HomeTabs"

    @Composable
    override fun Content() {
        val tabs = listOf(
            SSHTab,
            JellyfinTab,
        )

        val focusRequester = remember { FocusRequester() }
        val navigator = LocalNavigator.currentOrThrow
        var hasFocus by remember { mutableStateOf(false) }

        TabNavigator(
            tab = tabs.first(),
            key = TAB_NAVIGATOR_KEY,
        ) { tabNavigator ->
            val currentTabIndex = tabs.indexOf(tabNavigator.current)

            CompositionLocalProvider(LocalNavigator provides navigator) {
                Surface(
                    Modifier
                        .focusable()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            hasFocus = it.hasFocus
                        }
                        .onKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown) {
                                when {
                                    (event.isCtrlPressed && event.key == Key.DirectionLeft) -> {
                                        tabNavigator.current

                                        val newIndex = (currentTabIndex - 1).coerceAtLeast(0)
                                        tabNavigator.current = tabs[newIndex]
                                        true
                                    }
                                    (event.isCtrlPressed && event.key == Key.DirectionRight) -> {
                                        val newIndex = (currentTabIndex + 1).coerceAtMost(tabs.size - 1)
                                        tabNavigator.current = tabs[newIndex]
                                        true
                                    }
                                    else -> false
                                }
                            } else false
                        }
                ) {
                    Scaffold(
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

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
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
