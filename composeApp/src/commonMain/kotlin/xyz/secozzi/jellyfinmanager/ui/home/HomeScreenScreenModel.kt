package xyz.secozzi.jellyfinmanager.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.usecase.ServerUseCase

class HomeScreenScreenModel(
    private val serverUseCase: ServerUseCase,
) : ScreenModel {

    private val _selectedServer = MutableStateFlow<Server?>(null)
    val selectedServer = _selectedServer.asStateFlow()

    fun selectServer(server: Server) {
        _selectedServer.update { _ -> server }
    }

    val serverList: StateFlow<List<Server>> = serverUseCase.getServers()
        .onEach { servers ->
            _selectedServer.update { _ -> servers.firstOrNull() }
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList(),
        )
}
