package xyz.secozzi.jellyfinmanager.ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
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
import xyz.secozzi.jellyfinmanager.presentation.Screen

object PreferencesScreen : Screen() {
    private fun readResolve(): Any = PreferencesScreen

    @OptIn(ExperimentalMaterial3Api::class)
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
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SettingsMenuLink(
                    title = { Text(text = "Appearance") },
                    subtitle = { Text(text = "Dark mode, Material You") },
                    icon = { Icon(Icons.Outlined.Palette, null) },
                    onClick = { navigator.push(AppearancePreferencesScreen) }
                )

                SettingsMenuLink(
                    title = { Text(text = "SSH") },
                    subtitle = { Text(text = "SSH credentials") },
                    icon = { Icon(Icons.Outlined.Terminal, null) },
                    onClick = { navigator.push(SSHPreferencesScreen) }
                )
            }
        }
    }
}
