package xyz.secozzi.jellyfinmanager.ui.home


import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.usecase.ServerUseCase
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import xyz.secozzi.jellyfinmanager.presentation.utils.StateViewModel

class HomeScreenViewModel(
    private val serverUseCase: ServerUseCase,
) : StateViewModel<RequestState<List<Server>>>(RequestState.Idle) {

    private val _selectedServer = MutableStateFlow<Server?>(null)
    val selectedServer = _selectedServer.asStateFlow()

    fun selectServer(server: Server) {
        _selectedServer.update { _ -> server }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            serverUseCase.getServers().collectLatest { servers ->
                _selectedServer.update { _ -> servers.firstOrNull() }
                mutableState.update { _ -> RequestState.Success(servers) }
            }
        }
    }
}
