package xyz.secozzi.jellyfinmanager.ui.home.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.secozzi.jellyfinmanager.presentation.ErrorScreenContent
import xyz.secozzi.jellyfinmanager.presentation.FabButtonItem
import xyz.secozzi.jellyfinmanager.presentation.FabButtonMain
import xyz.secozzi.jellyfinmanager.presentation.FabButtonState
import xyz.secozzi.jellyfinmanager.presentation.LoadingScreenContent
import xyz.secozzi.jellyfinmanager.presentation.MaterialPullRefreshIndicator
import xyz.secozzi.jellyfinmanager.presentation.MultiFloatingActionButton
import xyz.secozzi.jellyfinmanager.presentation.components.ConfirmDialog
import xyz.secozzi.jellyfinmanager.presentation.components.Dialogs
import xyz.secozzi.jellyfinmanager.presentation.components.EditTextDialog
import xyz.secozzi.jellyfinmanager.presentation.rememberMultiFabState
import xyz.secozzi.jellyfinmanager.presentation.utils.Tab
import xyz.secozzi.jellyfinmanager.ui.preferences.PreferencesScreen
import xyz.secozzi.jellyfinmanager.ui.theme.spacing
import xyz.secozzi.jellyfinmanager.utils.Platform
import xyz.secozzi.jellyfinmanager.utils.platform

object SSHTab : Tab {
    private fun readResolve(): Any = JellyfinTab

    override val options: TabOptions
        @Composable
        get() {
            val image = rememberVectorPainter(Icons.Default.Terminal)
            return TabOptions(
                index = 0u,
                title = "SSH",
                icon = image,
            )
        }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = koinScreenModel<SSHTabScreenModel>()
        val currentDir by screenModel.currentDir.collectAsState()

        val state by screenModel.state.collectAsState()
        val dialogShown by screenModel.dialogShown.collectAsState()
        val isExecutingCommand by screenModel.executingCommand.collectAsState()

        var isRefreshing by remember { mutableStateOf(false) }
        val ptrState = rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                screenModel.screenModelScope.launch(Dispatchers.IO) {
                    screenModel.refresh()
                    isRefreshing = false
                }
            },
        )

        Scaffold(
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
            topBar = {
                TopAppBar(
                    title = { Text(currentDir, maxLines = 2) },
                    actions = {
                        if (platform == Platform.Desktop) {
                            IconButton(onClick = { screenModel.setDialog(Dialogs.EditText) }) {
                                Icon(Icons.Default.CreateNewFolder, null)
                            }

                            IconButton(onClick = { screenModel.setDialog(Dialogs.Confirm) }) {
                                Icon(Icons.Default.Delete, null)
                            }

                            IconButton(onClick = {
                                screenModel.screenModelScope.launch(Dispatchers.IO) {
                                    screenModel.refresh()
                                }
                            }) {
                                Icon(Icons.Default.Refresh, null)
                            }
                        }

                        IconButton(onClick = { navigator.push(PreferencesScreen) }) {
                            Icon(Icons.Rounded.Settings, null)
                        }
                    }
                )
            },
            floatingActionButton = {
                if (platform == Platform.Android) {

                    val fabState = rememberMultiFabState()

                    MultiFloatingActionButton(
                        items = listOf(
                            FabButtonItem(
                                iconRes = Icons.Default.CreateNewFolder,
                                label = "New folder",
                                key = "add",
                            ),
                            FabButtonItem(
                                iconRes = Icons.Default.Delete,
                                label = "Delete current",
                                key = "delete"
                            ),
                        ),
                        fabState = fabState,
                        onFabItemClicked = { btn ->
                            fabState.value = FabButtonState.Collapsed

                            when (btn.key) {
                                "add" -> screenModel.setDialog(Dialogs.EditText)
                                "delete" -> screenModel.setDialog(Dialogs.Confirm)
                                else -> {}
                            }
                        },
                        fabIcon = FabButtonMain(
                            iconRes = Icons.Default.Edit,
                            iconRotate = 90f,
                        ),
                    )
                }
            }
        ) { paddingValues ->
            state.DisplayResult(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onLoading = {
                    LoadingScreenContent(modifier = Modifier.fillMaxSize())
                },
                onIdle = {
                    LoadingScreenContent(modifier = Modifier.fillMaxSize())
                },
                onError = {
                    ErrorScreenContent(it) {
                        Box(
                            modifier = Modifier
                                .combinedClickable(
                                    onClick = { screenModel.connect() }
                                )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Icon(Icons.Rounded.Refresh, null, tint = MaterialTheme.colorScheme.primary)
                                Text("Retry", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                },
                onSuccess = { directories ->
                    PullRefreshLayout(
                        ptrState,
                        indicator = { MaterialPullRefreshIndicator(ptrState) },
                        modifier = Modifier.fillMaxSize(),
                        enabled = platform == Platform.Android,
                    ) {
                        LazyColumn {
                            itemsIndexed(directories, key = { _, d -> d.name }) { index, dir ->
                                FileListing(
                                    directory = dir,
                                    onClick = { screenModel.setDirectory(dir) },
                                    modifier = Modifier.background(
                                        if (index % 2 == 0) {
                                            MaterialTheme.colorScheme.surfaceContainerLow
                                        } else {
                                            MaterialTheme.colorScheme.surfaceContainerHigh
                                        },
                                    ),
                                )
                            }
                        }
                    }

                    when (dialogShown) {
                        Dialogs.Confirm -> {
                            ConfirmDialog(
                                title = "Delete",
                                isLoading = isExecutingCommand,
                                subtitle = "Are you sure you want to delete '${currentDir}'?",
                                onConfirm = {
                                    screenModel.removeDirectory()
                                },
                                onCancel = { screenModel.cancelCommand() },
                            )
                        }
                        Dialogs.EditText -> {
                            EditTextDialog(
                                title = "Create new directory",
                                isLoading = isExecutingCommand,
                                onConfirm = { newPath ->
                                    screenModel.createDirectory(newPath)
                                },
                                onCancel = { screenModel.cancelCommand() }
                            )
                        }
                        null -> {}
                    }
                }
            )
        }
    }

    @Composable
    fun FileListing(
        directory: Directory,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Row(
            modifier = modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(vertical = MaterialTheme.spacing.smaller, horizontal = MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = fileIcon(directory),
                contentDescription = null,
            )
            Column {
                Text(
                    text = directory.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (directory.isDirectory && directory.date == null) return@Column
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = directory.date ?: "",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (directory.extraData != null) {
                        Text(
                            text = directory.extraData,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun fileIcon(
        directory: Directory,
    ): ImageVector {
        if (directory.isDirectory) return Icons.Filled.Folder
        return when (directory.name.substringAfterLast(".")) {
            in listOf("jpg", "jpeg", "png", "webp") -> Icons.Filled.Image
            else -> Icons.AutoMirrored.Filled.InsertDriveFile
        }
    }
}
