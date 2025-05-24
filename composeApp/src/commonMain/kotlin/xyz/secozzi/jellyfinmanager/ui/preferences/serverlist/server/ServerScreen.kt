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
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.presentation.serverlist.server.ServerScreenContent
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Serializable
data class ServerRoute(
    val id: Long? = null,
)

@Composable
fun ServerScreen(serverId: Long? = null) {
    val navigator = LocalNavController.current
    val viewModel = koinViewModel<ServerScreenViewModel>()

    val state by viewModel.state.collectAsState()
    val server by viewModel.server.collectAsState()
    val serverNames by viewModel.serverNames.collectAsState()
    val isValid by viewModel.isValid.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Server")
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
            )
        },
        bottomBar = {
            if (state is ServerScreenViewModel.State.Success) {
                Button(
                    onClick = viewModel::saveServer,
                    enabled = isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                        .padding(horizontal = MaterialTheme.spacing.medium),
                ) {
                    when ((state as ServerScreenViewModel.State.Success).saveState) {
                        ServerScreenViewModel.State.SaveState.Error -> Icon(Icons.Default.Error, null)
                        ServerScreenViewModel.State.SaveState.Idle -> Text(
                            if (serverId ==
                                null
                            ) {
                                "Add server"
                            } else {
                                "Edit server"
                            },
                        )
                        ServerScreenViewModel.State.SaveState.Success -> Icon(Icons.Default.Check, null)
                    }
                }
            }
        },
    ) { contentPadding ->
        ServerScreenContent(
            state = state,
            server = server,
            serverNames = serverNames,
            onServerNameChange = viewModel::onServerNameChange,
            onJfAddressChange = viewModel::onJfAddressChange,
            onJfUsernameChange = viewModel::onJfUsernameChange,
            onJfPasswordChange = viewModel::onJfPasswordChange,
            onSSHAddressChange = viewModel::onSSHAddressChange,
            onSSHPortChange = viewModel::onSSHPortChange,
            onSSHHostnameChange = viewModel::onSSHHostnameChange,
            onSSHPasswordChange = viewModel::onSSHPasswordChange,
            onSSHPrivateKeyChange = viewModel::onSSHPrivateKeyChange,
            onSSHBaseDirChange = viewModel::onSSHBaseDirChange,
            onSSHBaseDirBlacklistChange = viewModel::onSSHBaseDirBlacklistChange,
            modifier = Modifier.padding(contentPadding),
        )
    }
}
