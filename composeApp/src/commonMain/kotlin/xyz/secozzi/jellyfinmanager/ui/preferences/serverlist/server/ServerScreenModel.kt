package xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.usecase.ServerUseCase

class ServerScreenModel(
    initialServer: Server?,
    private val serverNames: List<String>,
    private val serverUseCase: ServerUseCase,
) : StateScreenModel<ServerScreenModel.State>(State.Idle) {
    private val _server = MutableStateFlow<Server>(
        initialServer ?: Server(
            name = "",
            sshAddress = "",
            sshPort = 22L,
            sshHostname = "",
            sshPassword = "",
            sshPrivateKey = "",
            sshBaseDir = "",
            sshBaseDirBlacklist = "",
            jfAddress = "",
            jfUsername = "",
            jfPassword = "",
        )
    )
    val server = _server.asStateFlow()

    private val _isValid = MutableStateFlow(initialServer != null)
    val isValid = _isValid.asStateFlow()

    fun onServerNameChange(value: String) {
        _server.update { s -> s.copy(name = value) }
        updateIsValid()
    }

    fun onJfAddressChange(value: String) {
        _server.update { s -> s.copy(jfAddress = value) }
        updateIsValid()
    }

    fun onJfUsernameChange(value: String) {
        _server.update { s -> s.copy(jfUsername = value) }
        updateIsValid()
    }

    fun onJfPasswordChange(value: String) {
        _server.update { s -> s.copy(jfPassword = value) }
    }

    fun onSSHAddressChange(value: String) {
        _server.update { s -> s.copy(sshAddress = value) }
        updateIsValid()
    }

    fun onSSHPortChange(value: String) {
        _server.update { s -> s.copy(sshPort = value.toLongOrNull() ?: 0L) }
    }

    fun onSSHHostnameChange(value: String) {
        _server.update { s -> s.copy(sshHostname = value) }
        updateIsValid()
    }

    fun onSSHPasswordChange(value: String) {
        _server.update { s -> s.copy(sshPassword = value) }
    }

    fun onSSHPrivateKeyChange(value: String) {
        _server.update { s -> s.copy(sshPrivateKey = value) }
    }

    fun onSSHBaseDirChange(value: String) {
        _server.update { s -> s.copy(sshBaseDir = value) }
        updateIsValid()
    }

    fun onSSHBaseDirBlacklistChange(value: String) {
        _server.update { s -> s.copy(sshBaseDirBlacklist = value) }
    }

    private fun updateIsValid() {
        _isValid.update { _ ->
            server.value.name.isNotBlank() &&
                server.value.name !in serverNames &&
                server.value.jfAddress.isNotBlank() &&
                server.value.jfUsername.isNotBlank() &&
                server.value.sshAddress.isNotBlank() &&
                server.value.sshHostname.isNotBlank() &&
                server.value.sshBaseDir.isNotBlank()
        }
    }

    fun saveServer() {
        screenModelScope.launch(Dispatchers.IO) {
            try {
                val server = server.value

                serverUseCase.upsert(
                    Server(
                        id = server.id,
                        name = server.name,
                        index = server.index,
                        sshAddress = server.sshAddress,
                        sshPort = server.sshPort,
                        sshHostname = server.sshHostname,
                        sshPassword = server.sshPassword,
                        sshPrivateKey = server.sshPrivateKey,
                        sshBaseDir = server.sshBaseDir,
                        sshBaseDirBlacklist = server.sshBaseDirBlacklist,
                        jfAddress = server.jfAddress,
                        jfUsername = server.jfUsername,
                        jfPassword = server.jfPassword,
                    )
                )

                mutableState.update { _ -> State.Success }
            } catch (e: Exception) {
                mutableState.update { _ -> State.Error }
            }
        }
    }

    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data object Error : State

        @Immutable
        data object Success : State
    }
}
