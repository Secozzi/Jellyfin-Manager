package xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.core.parameter.parametersOf
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.presentation.serverlist.server.ServerScreen
import xyz.secozzi.jellyfinmanager.presentation.utils.Screen
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

class ServerScreen(
    private val initialServer: Server? = null,
    private val serverNames: List<String> = emptyList(),
) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ServerScreenModel>(
            parameters = { parametersOf(initialServer, serverNames) }
        )
        val state by screenModel.state.collectAsState()
        val server by screenModel.server.collectAsState()
        val isValid by screenModel.isValid.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Server")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    }
                )
            },
            bottomBar = {
                Button(
                    onClick = screenModel::saveServer,
                    enabled = isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                        .padding(horizontal = MaterialTheme.spacing.medium),
                ) {
                    when (state) {
                        is ServerScreenModel.State.Error -> Icon(Icons.Default.Error, null)
                        ServerScreenModel.State.Idle -> Text(if (initialServer == null) "Add server" else "Edit server")
                        ServerScreenModel.State.Success -> Icon(Icons.Default.Check, null)
                    }
                }
            }
        ) { contentPadding ->
            ServerScreen(
                server = server,
                serverNames = serverNames,
                onServerNameChange = screenModel::onServerNameChange,
                onJfAddressChange = screenModel::onJfAddressChange,
                onJfUsernameChange = screenModel::onJfUsernameChange,
                onJfPasswordChange = screenModel::onJfPasswordChange,
                onSSHAddressChange = screenModel::onSSHAddressChange,
                onSSHPortChange = screenModel::onSSHPortChange,
                onSSHHostnameChange = screenModel::onSSHHostnameChange,
                onSSHPasswordChange = screenModel::onSSHPasswordChange,
                onSSHPrivateKeyChange = screenModel::onSSHPrivateKeyChange,
                onSSHBaseDirChange = screenModel::onSSHBaseDirChange,
                onSSHBaseDirBlacklistChange = screenModel::onSSHBaseDirBlacklistChange,
                modifier = Modifier.padding(contentPadding),
            )
        }
    }
}
