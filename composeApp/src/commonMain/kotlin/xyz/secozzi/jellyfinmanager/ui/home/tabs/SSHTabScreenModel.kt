package xyz.secozzi.jellyfinmanager.ui.home.tabs

import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import net.schmizz.sshj.SSHClient
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import xyz.secozzi.jellyfinmanager.preferences.preference.asState
import xyz.secozzi.jellyfinmanager.presentation.components.Dialogs
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import xyz.secozzi.jellyfinmanager.presentation.utils.toRequestState
import xyz.secozzi.jellyfinmanager.utils.executeSSH
import xyz.secozzi.jellyfinmanager.utils.getSSHClient

data class Directory(
    val name: String,
    val isDirectory: Boolean,
    val date: String? = null,
    val extraData: String? = null,
)

class SSHTabScreenModel(
    private val preferences: BasePreferences,
) : StateScreenModel<RequestState<List<Directory>>>(RequestState.Idle) {
    private var sshClient: SSHClient? = null

    private val address by preferences.address.asState(screenModelScope)
    private val hostName by preferences.hostname.asState(screenModelScope)
    private val password by preferences.password.asState(screenModelScope)
    private val port by preferences.port.asState(screenModelScope)

    private val baseDir by preferences.baseDir.asState(screenModelScope)
    private val dirBlacklist by preferences.dirBlacklist.asState(screenModelScope)

    private val _currentDir = MutableStateFlow("")
    val currentDir = _currentDir.asStateFlow()

    private val _dialogShown = MutableStateFlow<Dialogs?>(null)
    val dialogShown = _dialogShown.asStateFlow()

    private val _executingCommand = MutableStateFlow(false)
    val executingCommand = _executingCommand.asStateFlow()

    private val lsRegex = Regex("""([d\-])[\w-]{9}\s+(\d+)\s+\S+\s+\S+\s+(\S+)\s+(\S+)\s+(\S+)\s+\S+\s+(.+)${'$'}""", RegexOption.MULTILINE)

    init {
        connect()
    }

    private var executingJob: Job? = null
    private var connectJob: Job? = null

    fun connect() {
        connectJob?.cancel()
        connectJob = screenModelScope.launch(Dispatchers.IO) {
            mutableState.update { _ -> RequestState.Loading }

            _currentDir.update { _ -> baseDir }

            try {
                sshClient?.disconnect()
                sshClient = getSSHClient(address, hostName, password, port)
                val directories = getDirectories(baseDir, dirBlacklist)
                mutableState.update { _ -> directories.toRequestState() }
            } catch (e: IOException) {
                mutableState.update { _ -> RequestState.Error(e) }
            }
        }
    }

    private suspend fun getDirectories(path: String, filter: String? = null): Result<List<Directory>> {
        val commandResult = try {
            if (sshClient?.isConnected == false) {
                sshClient = getSSHClient(address, hostName, password, port)
            }
            executeSSH(
                client = sshClient,
                commands = listOf("/usr/bin/ls", path, "-l1h", "--full-time", "--group-directories-first"),
            )
        } catch (e: IOException) {
            return Result.failure(e)
        }

        val directories = lsRegex.findAll(commandResult).map { m ->
            val (type, count, size, date, time, name) = m.destructured
            val isDirectory = type == "d"

            val extraData = if (isDirectory) {
                val dirCount = count.toInt() - 2
                val suffix = if (dirCount == 1) "item" else "items"

                "$dirCount $suffix"
            } else {
                val suffix = if (size.last().isDigit()) "B" else "iB"

                "$size$suffix"
            }

            val lastModified = "$date ${time.substringBefore(".")}"

            Directory(
                name = name.removePrefix("'").removeSuffix("'"),
                isDirectory = isDirectory,
                date = lastModified,
                extraData = extraData,
            )
        }.toList()

        val filtered =  if (filter == null) {
            directories
        } else {
            val blacklist = filter.split(',')
            directories.filterNot { it.name in blacklist }
        }
        return Result.success(filtered)
    }

    fun setDialog(dialog: Dialogs?) {
        _dialogShown.update { _ -> dialog }
    }

    fun cancelCommand() {
        executingJob?.cancel()
        _dialogShown.update { _ -> null }
        _executingCommand.update { _ -> false }
    }

    fun setDirectory(directory: Directory) {
        val newPath = if (directory.name == "..") {
            currentDir.value.substringBeforeLast("/")
        } else {
            "${currentDir.value}/${directory.name}"
        }
        _currentDir.update { _ -> newPath }

        screenModelScope.launch(Dispatchers.IO) {
            refresh()
        }
    }

    fun removeDirectory() {
        executeCommand(listOf("/usr/bin/rm", "-rf", currentDir.value))
    }

    fun createDirectory(directoryName: String) {
        val path = "${currentDir.value}/$directoryName"
        executeCommand(listOf("/usr/bin/mkdir", path))
    }

    private fun executeCommand(commands: List<String>) {
        executingJob?.cancel()
        executingJob = screenModelScope.launch(Dispatchers.IO) {
            _executingCommand.update { _ -> true }

            try {
                if (sshClient?.isConnected == false) {
                    sshClient = getSSHClient(address, hostName, password, port)
                }
                executeSSH(
                    client = sshClient,
                    commands = commands,
                )
            } catch (e: IOException) {
                mutableState.update { _ -> RequestState.Error(e) }
            } finally {
                withContext(NonCancellable) {
                    _dialogShown.update { _ -> null }
                    _executingCommand.update { _ -> false }
                }
            }

            refresh()
        }
    }

    suspend fun refresh() {
        mutableState.update { _ -> RequestState.Loading }

        val remoteDirectories = getDirectories(currentDir.value, dirBlacklist.takeIf { baseDir == currentDir.value })
        if (remoteDirectories.isFailure) {
            mutableState.update { _ -> RequestState.Error(remoteDirectories.exceptionOrNull() ?: Exception("error")) }
            return
        }

        val directories = buildList {
            if (currentDir.value != baseDir) {
                add(
                    Directory(name = "..", isDirectory = true)
                )
            }

            addAll(
                remoteDirectories.getOrThrow()
            )
        }

        mutableState.update { _ -> RequestState.Success(directories) }
    }

    override fun onDispose() {
        super.onDispose()
        screenModelScope.launch(Dispatchers.IO) {
            sshClient?.disconnect()
        }
    }
}
