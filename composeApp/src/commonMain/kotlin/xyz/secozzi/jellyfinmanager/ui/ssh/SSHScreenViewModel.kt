package xyz.secozzi.jellyfinmanager.ui.ssh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schmizz.sshj.SSHClient
import okio.IOException
import xyz.secozzi.jellyfinmanager.data.ssh.ExecuteSSH
import xyz.secozzi.jellyfinmanager.data.ssh.GetSSHClient
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.server.ServerStateHolder
import xyz.secozzi.jellyfinmanager.domain.ssh.GetDirectories
import xyz.secozzi.jellyfinmanager.domain.ssh.model.Directory
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState.Companion.toRequestState
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

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
    private val serverFlow = MutableStateFlow<Server?>(null)
    private val refreshFlow = MutableSharedFlow<Unit>()
    private val sshClient = MutableStateFlow<SSHClient?>(null)

    private val _pathList = MutableStateFlow<List<String>>(emptyList())
    val pathList = _pathList.asStateFlow()

    private val _dialogShown = MutableStateFlow<SSHDialogs?>(null)
    val dialogShown = _dialogShown.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val state = combine(
        serverFlow.filterNotNull(),
        _pathList,
        refreshFlow.onStart { emit(Unit) },
    ) { server, path, _ ->
        server to path
    }
        .debounce(50.milliseconds)
        .flatMapLatest { (server, path) ->
            flow {
                emit(RequestState.Loading)

                if (sshClient.value == null) {
                    try {
                        sshClient.update { _ -> getSSHClient(server) }
                    } catch (e: Exception) {
                        emit(RequestState.Error(e))
                        return@flow
                    }
                }

                val items = getDirectories(
                    sshClient = sshClient.value,
                    server = server,
                    path = path.joinToString(FILE_SEPARATOR),
                )

                emit(items.toRequestState())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = RequestState.Idle,
        )

    init {
        viewModelScope.launch {
            serverStateHolder.selectedServer.filterNotNull().collectLatest { selected ->
                sshClient.update { _ -> null }
                _pathList.update { _ -> listOf(selected.sshBaseDir) }

                serverFlow.update { _ -> selected }
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
        _pathList.update { p -> p + directory.name }
    }

    fun onNavigateTo(index: Int) {
        if (index == _pathList.value.lastIndex) {
            return
        }

        _pathList.update { p -> p.subList(0, index + 1) }
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
        val server = serverFlow.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            executeSSH.invoke(
                server = server,
                sshClient = sshClient.value,
                commands = commands,
            )

            refresh()
        }
    }

    companion object {
        private const val FILE_SEPARATOR = "/"
    }
}
