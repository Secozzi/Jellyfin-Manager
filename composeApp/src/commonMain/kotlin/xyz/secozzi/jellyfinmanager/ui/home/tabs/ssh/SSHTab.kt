package xyz.secozzi.jellyfinmanager.ui.home.tabs.ssh

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.secozzi.jellyfinmanager.presentation.components.ConfirmDialog
import xyz.secozzi.jellyfinmanager.presentation.components.EditTextDialog
import xyz.secozzi.jellyfinmanager.presentation.ssh.SSHScreen
import xyz.secozzi.jellyfinmanager.presentation.utils.Tab
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreenScreenModel

object SSHTab : Tab {
    private fun readResolve(): Any = SSHTab

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

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val homeScreenModel = koinScreenModel<HomeScreenScreenModel>()
        val screenModel = koinScreenModel<SSHTabScreenModel>()

        LaunchedEffect(Unit) {
            homeScreenModel.state.collect { selected ->
                selected.getSuccessDataOrNull()
                    ?.let(screenModel::changeServer)
            }
        }

        val state by screenModel.state.collectAsState()
        val dialogShown by screenModel.dialogShown.collectAsState()
        val pathList by screenModel.pathList.collectAsState()

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

        BackHandler(pathList.size > 1) {
            screenModel.onNavigateTo(pathList.size - 2)
        }

        when (dialogShown) {
            SSHDialogs.AddDirectory -> {
                EditTextDialog(
                    title = "Create new directory",
                    onConfirm = { newPath ->
                        screenModel.dismissDialog()
                        screenModel.createDirectory(newPath)
                    },
                    onCancel = { screenModel.dismissDialog() }
                )
            }
            is SSHDialogs.DeleteDirectory -> {
                val dir = (dialogShown as SSHDialogs.DeleteDirectory).directory

                ConfirmDialog(
                    title = "Delete",
                    subtitle = "Are you sure you want to delete '${dir.name}'?",
                    onConfirm = {
                        screenModel.dismissDialog()
                        screenModel.removeDirectory(dir)
                    },
                    onCancel = { screenModel.dismissDialog() },
                )
            }
            null -> {}
        }

        SSHScreen(
            state = state,
            pathList = pathList,
            ptrState = ptrState,
            onNavigateTo = screenModel::onNavigateTo,
            onClickDirectory = screenModel::onClickDirectory,
            onAdd = { screenModel.setDialog(SSHDialogs.AddDirectory) },
            onDelete = { screenModel.setDialog(SSHDialogs.DeleteDirectory(it)) },
            onRefresh = {
                screenModel.screenModelScope.launch(Dispatchers.IO) {
                    screenModel.refresh()
                }
            },
        )
    }
}
