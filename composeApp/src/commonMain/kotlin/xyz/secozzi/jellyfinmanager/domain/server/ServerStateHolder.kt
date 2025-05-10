package xyz.secozzi.jellyfinmanager.domain.server

import kotlinx.coroutines.flow.StateFlow
import xyz.secozzi.jellyfinmanager.domain.database.models.Server

interface ServerStateHolder {
    val selectedServer: StateFlow<Server?>

    fun updateServer(server: Server?)
}
