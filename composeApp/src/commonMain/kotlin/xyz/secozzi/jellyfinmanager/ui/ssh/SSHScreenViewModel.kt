package xyz.secozzi.jellyfinmanager.ui.ssh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dokar.sonner.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schmizz.sshj.SSHClient
import xyz.secozzi.jellyfinmanager.data.ssh.ExecuteSSH
import xyz.secozzi.jellyfinmanager.data.ssh.GetSSHClient
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.server.ServerStateHolder
import xyz.secozzi.jellyfinmanager.domain.ssh.GetDirectories
import xyz.secozzi.jellyfinmanager.domain.ssh.model.Directory
import xyz.secozzi.jellyfinmanager.presentation.utils.combineRefreshable

sealed interface SSHDialogs {
    data object AddDirectory : SSHDialogs
    data class DeleteDirectory(val directory: Directory) : SSHDialogs
}

class SSHScreenViewModel(
    private val getSSHClient: GetSSHClient,
    private val getDirectories: GetDirectories,
    private val executeSSH: ExecuteSSH,
    private val serverStateHolder: ServerStateHolder,
) : ViewModel() {
    private val refreshFlow = MutableSharedFlow<Unit>()
    private val sshClient = MutableStateFlow<SSHClient?>(null)

    private val _sshData = MutableStateFlow<SSHVMData>(SSHVMData.DEFAULT)
    val sshData = _sshData.asStateFlow()

    private val _dialogShown = MutableStateFlow<SSHDialogs?>(null)
    val dialogShown = _dialogShown.asStateFlow()

    private val _toasterEvent = MutableSharedFlow<Toast>()
    val toasterEvent = _toasterEvent.asSharedFlow()

    val state = combineRefreshable(
        sshData.filter { it.server != null },
        refreshFlow,
    ) { sshData ->
        if (sshClient.value == null) {
            sshClient.update { _ -> getSSHClient(sshData.server!!) }
        }

        getDirectories(
            sshClient = sshClient.value,
            server = sshData.server!!,
            path = sshData.pathList.joinToString(FILE_SEPARATOR),
        )
    }

    init {
        viewModelScope.launch {
            serverStateHolder.selectedServer.filterNotNull().collectLatest { selected ->
                sshClient.update { _ -> null }

                _sshData.update { _ ->
                    SSHVMData(
                        server = selected,
                        pathList = listOf(selected.sshBaseDir),
                    )
                }
            }
        }
    }

    fun setDialog(dialog: SSHDialogs?) {
        _dialogShown.update { _ -> dialog }
    }

    fun dismissDialog() {
        _dialogShown.update { _ -> null }
    }

    suspend fun refresh() {
        refreshFlow.emit(Unit)
    }

    fun onClickDirectory(directory: Directory) {
        _sshData.update { data ->
            data.copy(
                pathList = data.pathList + directory.name,
            )
        }
    }

    fun onNavigateTo(index: Int) {
        if (index == sshData.value.pathList.lastIndex) {
            return
        }

        _sshData.update { data ->
            data.copy(
                pathList = data.pathList.subList(0, index + 1),
            )
        }
    }

    fun createDirectory(path: String) {
        val newPath = (sshData.value.pathList + path).joinToString(FILE_SEPARATOR)

        executeCommand(
            commands = listOf(
                "mkdir",
                "-p",
                newPath,
            ),
            errorMessage = "Error creating directory",
        )
    }

    fun removeDirectory(directory: Directory) {
        val newPath = (sshData.value.pathList + directory.name).joinToString(FILE_SEPARATOR)

        executeCommand(
            commands = listOf(
                "rm",
                "-rf",
                newPath,
            ),
            errorMessage = "Error removing directory",
        )
    }

    private fun executeCommand(commands: List<String>, errorMessage: String) {
        val server = sshData.value.server ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                executeSSH.invoke(
                    server = server,
                    sshClient = sshClient.value,
                    commands = commands,
                )

                refresh()
            } catch (_: Exception) {
                _toasterEvent.emit(Toast(errorMessage))
            }
        }
    }

    data class SSHVMData(
        val server: Server?,
        val pathList: List<String>,
    ) {
        companion object {
            val DEFAULT = SSHVMData(server = null, pathList = emptyList())
        }
    }

    companion object {
        private const val FILE_SEPARATOR = "/"
    }
}
