package xyz.secozzi.jellyfinmanager.ui.home.tabs

import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import net.schmizz.sshj.SSHClient
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import xyz.secozzi.jellyfinmanager.preferences.preference.asState
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
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

    private val baseDir by preferences.baseDir.asState(screenModelScope)
    private val dirBlacklist by preferences.dirBlacklist.asState(screenModelScope)

    private val _currentDir = MutableStateFlow("")
    val currentDir = _currentDir.asStateFlow()

    private val lsRegex = Regex("""([d\-])[\w-]{9}\s+(\d+)\s+\S+\s+\S+\s+(\S+)\s+(\S+)\s+(\S+)\s+\S+\s+(.+)${'$'}""", RegexOption.MULTILINE)

    init {
        connect()
    }

    private var connectJob: Job? = null

    fun connect() {
        connectJob?.cancel()
        connectJob = screenModelScope.launch(Dispatchers.IO) {
            mutableState.update { _ -> RequestState.Loading }

            val address by preferences.address.asState(screenModelScope)
            val hostName by preferences.hostname.asState(screenModelScope)
            val password by preferences.password.asState(screenModelScope)
            val port by preferences.port.asState(screenModelScope)

            _currentDir.update { _ -> baseDir }

            try {
                sshClient?.disconnect()
                sshClient = getSSHClient(address, hostName, password, port)
                val directories = getDirectories(baseDir, dirBlacklist)
                mutableState.update { _ -> RequestState.Success(directories) }
            } catch (e: IOException) {
                mutableState.update { _ -> RequestState.Error(e) }
            }
        }
    }

    private suspend fun getDirectories(path: String, filter: String? = null): List<Directory> {
        val commandResult = executeSSH(sshClient, listOf("/usr/bin/ls", path, "-l1h", "--full-time", "--group-directories-first"))
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

        return if (filter == null) {
            directories
        } else {
            val blacklist = filter.split(',')
            directories.filterNot { it.name in blacklist }
        }
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

    suspend fun refresh() {
        mutableState.update { _ -> RequestState.Loading }

        val directories = buildList {
            if (currentDir.value != baseDir) {
                add(
                    Directory(name = "..", isDirectory = true)
                )
            }

            addAll(
                getDirectories(currentDir.value, dirBlacklist.takeIf { baseDir == currentDir.value })
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
