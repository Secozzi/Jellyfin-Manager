package xyz.secozzi.jellyfinmanager.ui.preferences.serverlist

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.usecase.ServerUseCase

class ServerListScreenModel(
    private val serverUseCase: ServerUseCase,
) : ScreenModel {
    private val _dialog = MutableStateFlow<ServerListDialog>(ServerListDialog.None)
    val dialog = _dialog.asStateFlow()

    val serverList: StateFlow<List<Server>> = serverUseCase.getServers()
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun moveUp(server: Server) {
        screenModelScope.launch(Dispatchers.IO) {
            serverUseCase.decreaseIndex(server)
        }
    }

    fun moveDown(server: Server) {
        screenModelScope.launch(Dispatchers.IO) {
            serverUseCase.increaseIndex(server)
        }
    }

    fun delete(server: Server) {
        screenModelScope.launch(Dispatchers.IO) {
            withContext(NonCancellable) {
                serverUseCase.delete(server)
            }
        }
    }

    fun showDialog(dialog: ServerListDialog) {
        _dialog.update { _ -> dialog }
    }

    fun dismissDialog() {
        _dialog.update { _ -> ServerListDialog.None }
    }

    sealed interface ServerListDialog {
        data object None : ServerListDialog
        data class Delete(val server: Server) : ServerListDialog
    }
}
