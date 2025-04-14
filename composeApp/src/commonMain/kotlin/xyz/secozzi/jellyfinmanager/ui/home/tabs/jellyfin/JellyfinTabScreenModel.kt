package xyz.secozzi.jellyfinmanager.ui.home.tabs.jellyfin

import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState

class JellyfinTabScreenModel : StateScreenModel<RequestState<Boolean>>(RequestState.Idle) {
    private val _selectedServer = MutableStateFlow<Server?>(null)
    val selectedServer = _selectedServer.asStateFlow()

    fun changeServer(server: Server?) {
        _selectedServer.update { _ -> server }
    }
}
