package xyz.secozzi.jellyfinmanager.ui.ssh

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schmizz.sshj.SSHClient
import xyz.secozzi.jellyfinmanager.data.ssh.ExecuteSSH
import xyz.secozzi.jellyfinmanager.data.ssh.GetSSHClient
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.ssh.GetDirectories
import xyz.secozzi.jellyfinmanager.domain.ssh.model.Directory
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState.Companion.toRequestState
import xyz.secozzi.jellyfinmanager.presentation.utils.StateViewModel
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreenViewModel
import java.io.IOException

sealed interface SSHDialogs {
    data object AddDirectory : SSHDialogs
    data class DeleteDirectory(val directory: Directory) : SSHDialogs
}

class SSHScreenViewModel(
    private val getSSHClient: GetSSHClient,
    private val getDirectories: GetDirectories,
    private val executeSSH: ExecuteSSH,
    private val homeViewModel: HomeScreenViewModel,
) : StateViewModel<RequestState<List<Directory>>>(RequestState.Idle) {

    private val currentServer = MutableStateFlow<Server?>(null)

    private val _pathList = MutableStateFlow<List<String>>(emptyList())
    val pathList = _pathList.asStateFlow()

    private val _dialogShown = MutableStateFlow<SSHDialogs?>(null)
    val dialogShown = _dialogShown.asStateFlow()

    private var sshClient: SSHClient? = null

    fun setDialog(dialog: SSHDialogs?) {
        _dialogShown.update { _ -> dialog }
    }

    init {
        viewModelScope.launch {
            homeViewModel.selectedServer.collect { selected ->
                selected?.let(::changeServer)
            }
        }
    }

    fun changeServer(server: Server?) {
        sshClient = null
        mutableState.update { _ -> RequestState.Loading }

        server?.let { s ->
            currentServer.update { _ -> s }
            _pathList.update { _ -> listOf(s.sshBaseDir) }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    sshClient = getSSHClient(s)
                    refresh()
                } catch (e: IOException) {
                    mutableState.update { _ -> RequestState.Error(e) }
                }
            }
        }
    }

    suspend fun refresh() {
        val server = currentServer.value ?: return

        mutableState.update { _ -> RequestState.Loading }
        val directories = getDirectories(
            sshClient = sshClient,
            server = server,
            path = _pathList.value.joinToString(FILE_SEPARATOR),
        )
        mutableState.update { _ -> directories.toRequestState() }
    }

    fun onClickDirectory(directory: Directory) {
        viewModelScope.launch(Dispatchers.IO) {
            _pathList.update { p -> p + directory.name }
            refresh()
        }
    }

    fun onNavigateTo(index: Int) {
        if (index == _pathList.value.lastIndex) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _pathList.update { p -> p.subList(0, index + 1) }
            refresh()
        }
    }

    fun createDirectory(path: String) {
        val newPath = (_pathList.value + path).joinToString(FILE_SEPARATOR)

        executeCommand(
            listOf(
                "mkdir",
                "-p",
                newPath,
            ),
        )
    }

    fun removeDirectory(directory: Directory) {
        val newPath = (_pathList.value + directory.name).joinToString(FILE_SEPARATOR)

        executeCommand(
            listOf(
                "rm",
                "-rf",
                newPath
            )
        )
    }

    private fun executeCommand(commands: List<String>) {
        val server = currentServer.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            mutableState.update { _ -> RequestState.Loading }

            executeSSH.invoke(
                server = server,
                sshClient = sshClient,
                commands = commands,
            )

            refresh()
        }
    }

    fun dismissDialog() {
        _dialogShown.update { _ -> null }
    }

    companion object {
        private const val FILE_SEPARATOR = "/"
    }
}
