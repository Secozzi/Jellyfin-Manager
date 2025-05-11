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
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState

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

    private val _state = MutableStateFlow<RequestState<List<Server>>>(RequestState.Idle)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            serverUseCase.getServers().collectLatest { servers ->
                _isLoaded.update { _ -> true }
                serverStateHolder.updateServer(servers.firstOrNull())
                _state.update { _ ->
                    servers.takeIf { it.isNotEmpty() }?.let { RequestState.Success(it) }
                        ?: RequestState.Error(Exception("No server available"))
                }
            }
        }
    }
}
