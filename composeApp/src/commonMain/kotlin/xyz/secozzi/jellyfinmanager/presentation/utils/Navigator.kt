package xyz.secozzi.jellyfinmanager.presentation.utils

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.Navigator

abstract class Screen : Screen {
    final override val key = uniqueScreenKey
}

interface Tab : cafe.adriel.voyager.navigator.tab.Tab {
    suspend fun onReselect(navigator: Navigator) {}
}
