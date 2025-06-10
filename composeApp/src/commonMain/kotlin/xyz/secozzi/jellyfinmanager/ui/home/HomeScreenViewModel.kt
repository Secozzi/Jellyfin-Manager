package xyz.secozzi.jellyfinmanager.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.server.ServerStateHolder
import xyz.secozzi.jellyfinmanager.domain.usecase.ServerUseCase
import xyz.secozzi.jellyfinmanager.presentation.utils.UiState

class HomeScreenViewModel(
    private val serverUseCase: ServerUseCase,
    private val serverStateHolder: ServerStateHolder,
) : ViewModel() {
    val selectedServer = serverStateHolder.selectedServer

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded.asStateFlow()

    fun selectServer(server: Server) {
        serverStateHolder.updateServer(server)
    }

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _servers = MutableStateFlow<List<Server>>(emptyList())
    val servers = _servers.asStateFlow()

    init {
        viewModelScope.launch {
            serverUseCase.getServers().collectLatest { servers ->
                _isLoaded.update { _ -> true }
                serverStateHolder.updateServer(servers.firstOrNull())
                _servers.update { _ -> servers }

                if (servers.isEmpty()) {
                    _uiState.update { _ -> UiState.Error(Exception("No server available")) }
                } else {
                    _uiState.update { _ -> UiState.Success(Unit) }
                }
            }
        }
    }
}
