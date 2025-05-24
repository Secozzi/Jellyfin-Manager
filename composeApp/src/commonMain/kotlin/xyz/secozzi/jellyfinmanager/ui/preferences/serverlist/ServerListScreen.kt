package xyz.secozzi.jellyfinmanager.ui.preferences.serverlist

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.presentation.serverlist.ServerListScreenContent
import xyz.secozzi.jellyfinmanager.presentation.serverlist.components.ServerListDeleteDialog
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server.ServerRoute

@Serializable
data object ServerListRoute

@Composable
fun ServerListScreen() {
    val navigator = LocalNavController.current

    val viewModel = koinViewModel<ServerListScreenViewModel>()
    val state by viewModel.state.collectAsState()
    val dialog by viewModel.dialog.collectAsState()

    when (dialog) {
        ServerListScreenViewModel.ServerListDialog.None -> {}
        is ServerListScreenViewModel.ServerListDialog.Delete -> {
            val server = (dialog as ServerListScreenViewModel.ServerListDialog.Delete).server
            ServerListDeleteDialog(
                onDismissRequest = viewModel::dismissDialog,
                onDelete = { viewModel.delete(server) },
                title = server.name,
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Server list")
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navigator.navigate(ServerRoute(null)) },
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("Add") },
            )
        },
    ) { contentPadding ->
        ServerListScreenContent(
            state = state,
            onClickEdit = {
                navigator.navigate(ServerRoute(it.id))
            },
            onClickDelete = { viewModel.showDialog(ServerListScreenViewModel.ServerListDialog.Delete(it)) },
            onClickMoveUp = viewModel::moveUp,
            onClickMoveDown = viewModel::moveDown,
            modifier = Modifier.padding(contentPadding),
        )
    }
}
