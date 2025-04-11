package xyz.secozzi.jellyfinmanager.ui.preferences.serverlist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import xyz.secozzi.jellyfinmanager.presentation.serverlist.ServerListScreen
import xyz.secozzi.jellyfinmanager.presentation.serverlist.components.ServerListDeleteDialog
import xyz.secozzi.jellyfinmanager.presentation.utils.Screen
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server.ServerScreen

object ServerListScreen : Screen() {
    private fun readResolve(): Any = ServerListScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = koinScreenModel<ServerListScreenModel>()
        val serverList by screenModel.serverList.collectAsState()
        val dialog by screenModel.dialog.collectAsState()

        when (dialog) {
            ServerListScreenModel.ServerListDialog.None -> {}
            is ServerListScreenModel.ServerListDialog.Delete -> {
                val server = (dialog as ServerListScreenModel.ServerListDialog.Delete).server
                ServerListDeleteDialog(
                    onDismissRequest = screenModel::dismissDialog,
                    onDelete = { screenModel.delete(server) },
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
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        val names = serverList.map { it.name }
                        navigator.push(ServerScreen(null, names))
                    },
                    icon = { Icon(Icons.Filled.Add, null) },
                    text = { Text("Add") },
                )
            }
        ) { contentPadding ->
            ServerListScreen(
                serverList = serverList,
                onClickEdit = {
                    val names = serverList.map { s -> s.name } - it.name
                    navigator.push(ServerScreen(it, names))
                },
                onClickDelete = { screenModel.showDialog(ServerListScreenModel.ServerListDialog.Delete(it)) },
                onClickMoveUp = screenModel::moveUp,
                onClickMoveDown = screenModel::moveDown,
                modifier = Modifier.padding(contentPadding),
            )
        }
    }
}
