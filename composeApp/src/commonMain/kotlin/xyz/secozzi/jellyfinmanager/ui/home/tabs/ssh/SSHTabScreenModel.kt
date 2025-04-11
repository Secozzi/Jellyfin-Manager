package xyz.secozzi.jellyfinmanager.ui.home.tabs.ssh

import cafe.adriel.voyager.core.model.StateScreenModel
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState

class SSHTabScreenModel(
    private val server: Server,
) : StateScreenModel<RequestState<Boolean>>(RequestState.Idle) {

}
