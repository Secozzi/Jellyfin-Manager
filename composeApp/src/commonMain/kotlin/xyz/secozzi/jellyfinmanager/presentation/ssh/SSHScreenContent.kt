package xyz.secozzi.jellyfinmanager.presentation.ssh

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Downloading
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.PullRefreshState
import xyz.secozzi.jellyfinmanager.domain.ssh.model.Directory
import xyz.secozzi.jellyfinmanager.presentation.components.MaterialPullRefreshIndicator
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreenContent
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreenContent
import xyz.secozzi.jellyfinmanager.presentation.ssh.components.PathLevelIndication
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import xyz.secozzi.jellyfinmanager.ui.theme.spacing
import xyz.secozzi.jellyfinmanager.utils.Platform
import xyz.secozzi.jellyfinmanager.utils.platform

@Composable
fun SSHScreenContent(
    state: RequestState<List<Directory>>,
    pathList: List<String>,
    ptrState: PullRefreshState,
    onClickDirectory: (Directory) -> Unit,
    onNavigateTo: (Int) -> Unit,
    onAdd: () -> Unit,
    onDelete: (Directory) -> Unit,
    onRefresh: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Filled.Add, null)
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .exclude(NavigationBarDefaults.windowInsets),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PathLevelIndication(
                    pathList = pathList,
                    onNavigateTo = onNavigateTo,
                    modifier = Modifier.weight(1f),
                )

                if (platform == Platform.Desktop) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.padding(end = MaterialTheme.spacing.medium)
                    ) {
                        Icon(Icons.Default.Refresh, null)
                    }
                }
            }

            if (state.isLoading() || state.isIdle()) {
                LoadingScreenContent()
                return@Scaffold
            }

            if (state.isError()) {
                ErrorScreenContent(
                    error = state.getError(),
                    modifier = Modifier.fillMaxSize(),
                )
                return@Scaffold
            }

            val directories = state.getSuccessData()

            PullRefreshLayout(
                state = ptrState,
                indicator = { MaterialPullRefreshIndicator(ptrState) },
                modifier = Modifier.fillMaxSize(),
                enabled = platform == Platform.Android,
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                ) {
                    items(directories) { directory ->
                        ListItem(
                            modifier = if (directory.isDirectory) {
                                Modifier.combinedClickable(
                                    onClick = { onClickDirectory(directory) },
                                    onLongClick = { onDelete(directory) },
                                )
                            } else {
                                Modifier
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = if (directory.isDirectory) {
                                        Icons.Outlined.Folder
                                    } else {
                                        getIcon(directory.name.substringAfterLast("."))
                                    },
                                    null,
                                )
                            },
                            headlineContent = {
                                Text(text = directory.name)
                            },
                            supportingContent = {
                                directory.extraData?.let {
                                    Text(it)
                                }
                            },
                            trailingContent = {
                                IconButton(onClick = { onDelete(directory) }) {
                                    Icon(Icons.Outlined.Delete, null)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun getIcon(extension: String): ImageVector {
    return when (extension) {
        "jpg", "jpeg", "png", "webp" -> Icons.Outlined.Image
        "avi", "flv", "mkv", "mov", "mp4", "webm", "wmv" -> Icons.Outlined.Movie
        "json", "nfo" -> Icons.Outlined.Code
        "!qB" -> Icons.Outlined.Downloading
        else -> Icons.Outlined.Description
    }
}
