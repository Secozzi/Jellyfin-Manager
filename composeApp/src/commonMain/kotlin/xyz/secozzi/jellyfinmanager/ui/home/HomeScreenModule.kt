package xyz.secozzi.jellyfinmanager.ui.home

import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import xyz.secozzi.jellyfinmanager.preferences.preference.asState
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState

class HomeScreenModel(
    private val preferences: BasePreferences,
) : StateScreenModel<RequestState<List<String>>>(RequestState.Idle) {
    init {
        if (mutableState.value is RequestState.Idle) {
            screenModelScope.launch(Dispatchers.IO) {
                getLibraries()
            }
        }
    }

    fun getLibraries() {
        mutableState.update { _ -> RequestState.Loading }

        val address by preferences.address.asState(screenModelScope)
        val port by preferences.port.asState(screenModelScope)
        val hostName by preferences.hostname.asState(screenModelScope)
        val password by preferences.password.asState(screenModelScope)
        val baseDir by preferences.baseDir.asState(screenModelScope)
        val dirBlacklist by preferences.dirBlacklist.asState(screenModelScope)

        mutableState.update { _ ->
            try {
                val blacklist = dirBlacklist.split(",")

                /*
                val dirs = executeSSH(
                    address = address,
                    hostName = hostName,
                    password = password,
                    port = port,
                    commands = listOf(
                        "/usr/bin/ls",
                        baseDir,
                        "-1",
                    ),
                ).let { result ->
                    getDirs(result).filterNot { it in blacklist }
                }

                 */

                val dirs = listOf("Anime", "Airing")

                RequestState.Success(dirs)
            } catch (e: Exception) {
                RequestState.Error(e)
            }
        }

    }
}
