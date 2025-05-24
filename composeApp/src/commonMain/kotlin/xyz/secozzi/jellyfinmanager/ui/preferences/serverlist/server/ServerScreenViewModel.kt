package xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.usecase.ServerUseCase

class ServerScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val serverUseCase: ServerUseCase,
) : ViewModel() {
    private val serverRoute = savedStateHandle.toRoute<ServerRoute>()

    private val _server = MutableStateFlow<Server>(Server.getUninitializedServer())
    val server = _server.asStateFlow()

    private val _serverNames = MutableStateFlow<List<String>>(emptyList())
    val serverNames = _serverNames.asStateFlow()

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            serverUseCase.getServers().take(1).collectLatest { servers ->
                servers.firstOrNull { it.id == serverRoute.id }?.let {
                    _server.update { _ -> it }
                }

                _serverNames.update { _ ->
                    servers.filterNot { it.id == serverRoute.id }.map { it.name }
                }

                _state.update { _ -> State.Success(State.SaveState.Idle) }
            }
        }
    }

    private val _isValid = MutableStateFlow(false)
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
        updateIsValid()
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
                server.value.name !in serverNames.value &&
                server.value.jfAddress.isNotBlank() &&
                server.value.jfUsername.isNotBlank() &&
                server.value.sshAddress.isNotBlank() &&
                server.value.sshHostname.isNotBlank() &&
                server.value.sshBaseDir.isNotBlank()
        }
    }

    fun saveServer() {
        viewModelScope.launch(Dispatchers.IO) {
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
                    ),
                )

                _state.update { _ -> State.Success(State.SaveState.Success) }
            } catch (e: Exception) {
                _state.update { _ -> State.Success(State.SaveState.Error) }
            }
        }
    }

    sealed interface State {
        @Immutable
        data object Loading : State

        @Immutable
        data class Success(
            val saveState: SaveState,
        ) : State

        enum class SaveState {
            Idle,
            Error,
            Success,
        }
    }
}
