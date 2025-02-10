package xyz.secozzi.jellyfinmanager.ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import xyz.secozzi.jellyfinmanager.preferences.preference.collectAsState
import xyz.secozzi.jellyfinmanager.presentation.Screen
import xyz.secozzi.jellyfinmanager.presentation.preferences.PasswordPreference
import xyz.secozzi.jellyfinmanager.presentation.preferences.TextFieldPreference
import xyz.secozzi.jellyfinmanager.utils.Platform
import xyz.secozzi.jellyfinmanager.utils.platform

object SSHPreferencesScreen : Screen()  {
    private fun readResolve(): Any = SSHPreferencesScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val preferences = koinInject<BasePreferences>()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("SSH")
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
                val address by preferences.address.collectAsState()
                TextFieldPreference(
                    value = address,
                    onValueChange = { preferences.address.set(it) },
                    title = { Text(text = "Address") },
                    summary = { Text(text = address) },
                    textToValue = { it },
                )

                val port by preferences.port.collectAsState()
                TextFieldPreference(
                    value = port,
                    onValueChange = { preferences.port.set(it) },
                    title = { Text(text = "Port") },
                    summary = { Text(text = port.toString()) },
                    textToValue = { it.toInt() },
                    isValid = { it.toIntOrNull() != null },
                    errorText = { Text("Port must be an integer") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                val hostName by preferences.hostname.collectAsState()
                TextFieldPreference(
                    value = hostName,
                    onValueChange = { preferences.hostname.set(it) },
                    title = { Text(text = "Hostname") },
                    summary = { Text(text = hostName) },
                    textToValue = { it },
                )

                val password by preferences.password.collectAsState()
                PasswordPreference(
                    value = password,
                    onValueChange = { preferences.password.set(it) },
                    title = { Text(text = "Password") },
                    textToValue = { it }
                )

                val baseDir by preferences.baseDir.collectAsState()
                TextFieldPreference(
                    value = baseDir,
                    onValueChange = { preferences.baseDir.set(it) },
                    title = { Text(text = "Base directory") },
                    summary = { Text(text = baseDir) },
                    textToValue = { it },
                )

                val dirBlacklist by preferences.dirBlacklist.collectAsState()
                TextFieldPreference(
                    value = dirBlacklist,
                    onValueChange = { preferences.dirBlacklist.set(it) },
                    title = { Text(text = "Base directory blacklist") },
                    summary = { Text(text = dirBlacklist) },
                    textToValue = { it },
                )
            }
        }
    }
}
