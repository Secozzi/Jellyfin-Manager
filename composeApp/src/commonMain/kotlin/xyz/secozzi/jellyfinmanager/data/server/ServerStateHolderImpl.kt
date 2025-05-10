package xyz.secozzi.jellyfinmanager.data.server

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.server.ServerStateHolder

class ServerStateHolderImpl : ServerStateHolder {
    private val _selectedServer = MutableStateFlow<Server?>(null)
    override val selectedServer: StateFlow<Server?> = _selectedServer.asStateFlow()

    override fun updateServer(server: Server?) {
        _selectedServer.update { _ -> server }
    }
}
