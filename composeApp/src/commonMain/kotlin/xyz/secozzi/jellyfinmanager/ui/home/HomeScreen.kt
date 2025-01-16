package xyz.secozzi.jellyfinmanager.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import xyz.secozzi.jellyfinmanager.presentation.Screen
import xyz.secozzi.jellyfinmanager.ui.preferences.PreferencesScreen

object HomeScreen : Screen() {
    private fun readResolve(): Any = HomeScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
            topBar = {
                TopAppBar(
                    title = { Text("Home") },
                    actions = {
                        IconButton(onClick = { navigator.push(PreferencesScreen) }) {
                            Icon(Icons.Rounded.Settings, null)
                        }
                    }
                )
            },
        ) { paddingValues ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                Text("Home Screen")
            }
        }
    }
}
