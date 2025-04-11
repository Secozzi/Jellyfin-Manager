package xyz.secozzi.jellyfinmanager.ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.alorma.compose.settings.ui.SettingsMenuLink
import xyz.secozzi.jellyfinmanager.presentation.utils.Screen
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.ServerListScreen

object PreferencesScreen : Screen() {
    private fun readResolve(): Any = PreferencesScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Preferences")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    }
                )
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                SettingsMenuLink(
                    title = { Text(text = "Appearance") },
                    subtitle = { Text(text = "Dark mode, Material You") },
                    icon = { Icon(Icons.Outlined.Palette, null) },
                    onClick = { navigator.push(AppearancePreferencesScreen) }
                )

                SettingsMenuLink(
                    title = { Text(text = "Servers") },
                    subtitle = { Text(text = "Configure server list") },
                    icon = { Icon(Icons.Outlined.Dns, null) },
                    onClick = { navigator.push(ServerListScreen) }
                )
            }
        }
    }
}
