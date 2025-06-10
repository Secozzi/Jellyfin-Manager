package xyz.secozzi.jellyfinmanager.presentation.serverlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreen
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreen
import xyz.secozzi.jellyfinmanager.presentation.serverlist.components.ServerListItem
import xyz.secozzi.jellyfinmanager.presentation.utils.UiState
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun ServerListScreenContent(
    servers: UiState<List<Server>>,
    onClickEdit: (Server) -> Unit,
    onClickDelete: (Server) -> Unit,
    onClickMoveUp: (Server) -> Unit,
    onClickMoveDown: (Server) -> Unit,
    paddingValues: PaddingValues,
) {
    val lazyListState = rememberLazyListState()

    if (servers.isWaiting()) {
        LoadingScreen(paddingValues)
        return
    }

    if (servers.isError()) {
        ErrorScreen(
            error = servers.getError(),
            paddingValues = paddingValues,
        )
        return
    }

    if (servers.getData().isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "You have no servers. Tap the plus button to add a server.",
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    ServerListContent(
        serverList = servers.getData(),
        lazyListState = lazyListState,
        onClickEdit = onClickEdit,
        onClickDelete = onClickDelete,
        onMoveUp = onClickMoveUp,
        onMoveDown = onClickMoveDown,
        modifier = Modifier.padding(paddingValues).padding(
            top = MaterialTheme.spacing.small,
            start = MaterialTheme.spacing.medium,
            end = MaterialTheme.spacing.medium,
        ),
    )
}

@Composable
private fun ServerListContent(
    serverList: List<Server>,
    lazyListState: LazyListState,
    onClickEdit: (Server) -> Unit,
    onClickDelete: (Server) -> Unit,
    onMoveUp: (Server) -> Unit,
    onMoveDown: (Server) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        modifier = modifier,
    ) {
        itemsIndexed(
            items = serverList,
            key = { _, server -> "server-${server.id}" },
        ) { index, server ->
            ServerListItem(
                server = server,
                canMoveUp = index != 0,
                canMoveDown = index != serverList.lastIndex,
                onMoveUp = { onMoveUp(server) },
                onMoveDown = { onMoveDown(server) },
                onEdit = { onClickEdit(server) },
                onDelete = { onClickDelete(server) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}
