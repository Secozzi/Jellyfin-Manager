package xyz.secozzi.jellyfinmanager.ui.ssh

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.dokar.sonner.Toaster
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.presentation.components.ConfirmDialog
import xyz.secozzi.jellyfinmanager.presentation.components.EditTextDialog
import xyz.secozzi.jellyfinmanager.presentation.ssh.SSHScreenContent
import xyz.secozzi.jellyfinmanager.ui.providers.LocalToaster

@Serializable
data object SSHRoute

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SSHScreen() {
    val toaster = LocalToaster.current
    val viewModel = koinViewModel<SSHScreenViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val toasterEvent by viewModel.toasterEvent.collectAsStateWithLifecycle(null)
    val dialogShown by viewModel.dialogShown.collectAsStateWithLifecycle()
    val sshData by viewModel.sshData.collectAsStateWithLifecycle()

    LaunchedEffect(toasterEvent) {
        toasterEvent?.let { toaster.show(it) }
    }

    var isRefreshing by remember { mutableStateOf(false) }
    val ptrState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                viewModel.refresh()
                isRefreshing = false
            }
        },
    )

    Toaster(state = toaster)

    BackHandler(sshData.pathList.size > 1) {
        viewModel.onNavigateTo(sshData.pathList.size - 2)
    }

    when (dialogShown) {
        SSHDialogs.AddDirectory -> {
            EditTextDialog(
                title = "Create new directory",
                onConfirm = { newPath ->
                    viewModel.dismissDialog()
                    viewModel.createDirectory(newPath)
                },
                onCancel = { viewModel.dismissDialog() },
            )
        }
        is SSHDialogs.DeleteDirectory -> {
            val dir = (dialogShown as SSHDialogs.DeleteDirectory).directory

            ConfirmDialog(
                title = "Delete",
                subtitle = "Are you sure you want to delete '${dir.name}'?",
                onConfirm = {
                    viewModel.dismissDialog()
                    viewModel.removeDirectory(dir)
                },
                onCancel = { viewModel.dismissDialog() },
            )
        }
        null -> {}
    }

    SSHScreenContent(
        state = state,
        pathList = sshData.pathList,
        ptrState = ptrState,
        onNavigateTo = viewModel::onNavigateTo,
        onClickDirectory = viewModel::onClickDirectory,
        onAdd = { viewModel.setDialog(SSHDialogs.AddDirectory) },
        onDelete = { viewModel.setDialog(SSHDialogs.DeleteDirectory(it)) },
        onRefresh = {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                viewModel.refresh()
            }
        },
    )
}
