package xyz.secozzi.jellyfinmanager.ui.home

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.usecase.ServerUseCase
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState

class HomeScreenScreenModel(
    private val serverUseCase: ServerUseCase,
) : StateScreenModel<RequestState<Server?>>(RequestState.Idle) {

    fun selectServer(server: Server) {
        mutableState.update { _ -> RequestState.Success(server) }
    }

    val serverList: StateFlow<List<Server>> = serverUseCase.getServers()
        .onEach { servers ->
            mutableState.update { _ -> RequestState.Success(servers.firstOrNull()) }
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList(),
        )
}
