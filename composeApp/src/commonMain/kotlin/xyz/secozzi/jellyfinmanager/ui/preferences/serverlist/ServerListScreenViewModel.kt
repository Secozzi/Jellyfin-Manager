package xyz.secozzi.jellyfinmanager.ui.preferences.serverlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.usecase.ServerUseCase
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState

class ServerListScreenViewModel(
    private val serverUseCase: ServerUseCase,
) : ViewModel() {
    private val _dialog = MutableStateFlow<ServerListDialog>(ServerListDialog.None)
    val dialog = _dialog.asStateFlow()

    val state: StateFlow<RequestState<List<Server>>> = serverUseCase.getServers()
        .map { RequestState.Success(it) }
        .catch { RequestState.Error(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = RequestState.Idle,
        )

    fun moveUp(server: Server) {
        viewModelScope.launch(Dispatchers.IO) {
            serverUseCase.decreaseIndex(server)
        }
    }

    fun moveDown(server: Server) {
        viewModelScope.launch(Dispatchers.IO) {
            serverUseCase.increaseIndex(server)
        }
    }

    fun delete(server: Server) {
        viewModelScope.launch(Dispatchers.IO) {
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
