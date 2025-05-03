package xyz.secozzi.jellyfinmanager.ui.home.tabs.jellyfin

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemKind
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinUser
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState

class JellyfinTabScreenModel(
    private val jellyfinRepository: JellyfinRepository,
) : StateScreenModel<RequestState<JellyfinItems>>(RequestState.Idle) {
    private val _selectedServer = MutableStateFlow<Server?>(null)
    val selectedServer = _selectedServer.asStateFlow()

    private val _itemList = MutableStateFlow<List<Pair<String, UUID?>>>(emptyList())
    val itemList = _itemList.asStateFlow()

    private val user = MutableStateFlow<JellyfinUser?>(null)

    fun changeServer(server: Server?) {
        mutableState.update { _ -> RequestState.Loading }

        server?.let { s ->
            _selectedServer.update { _ -> s }
            screenModelScope.launch {
                user.update { _ -> jellyfinRepository.loadServer(s) }
                _itemList.update { _ -> listOf(Pair("Home", null)) }

                val libraries = jellyfinRepository.getLibraries(user.value!!)
                mutableState.update { _ ->
                    RequestState.Success(
                        JellyfinItems.Libraries(libraries)
                    )
                }
            }
        }
    }

    fun onNavigateTo(index: Int) {
        if (index == itemList.value.lastIndex) {
            return
        }

        mutableState.update { _ -> RequestState.Loading }
        _itemList.update { i -> i.subList(0, index + 1) }

        screenModelScope.launch {
            when (itemList.value.size) {
                1 -> {
                    val libraries = jellyfinRepository.getLibraries(user.value!!)
                    mutableState.update { _ ->
                        RequestState.Success(
                            JellyfinItems.Libraries(libraries)
                        )
                    }
                }
                else -> {
                    val currentItem = itemList.value.last()
                    val items = jellyfinRepository.getItems(user.value!!, currentItem.second)
                    mutableState.update { _ ->
                        RequestState.Success(
                            if (itemList.value.size == 2) {
                                JellyfinItems.Series(items)
                            } else {
                                JellyfinItems.Seasons(items)
                            }
                        )
                    }
                }
            }
        }
    }

    fun onClickItem(item: JellyfinItem) {
        mutableState.update { _ -> RequestState.Loading }
        _itemList.update { i -> i + Pair(item.name, item.id) }

        screenModelScope.launch {
            val items = jellyfinRepository.getItems(user.value!!, item.id)
            mutableState.update { _ ->
                when (item.type) {
                    BaseItemKind.COLLECTION_FOLDER -> RequestState.Success(JellyfinItems.Series(items))
                    BaseItemKind.SERIES -> RequestState.Success(JellyfinItems.Seasons(items))
                    BaseItemKind.SEASON -> RequestState.Success(JellyfinItems.Episodes(items, item))
                    BaseItemKind.MOVIE -> RequestState.Success(JellyfinItems.Episodes(items, item))
                    else -> RequestState.Error(Throwable("Invalid type"))
                }
            }
        }
    }
}

sealed class JellyfinItems(open val items: List<JellyfinItem>) {
    data class Libraries(override val items: List<JellyfinItem>) : JellyfinItems(items)
    data class Series(override val items: List<JellyfinItem>) : JellyfinItems(items)
    data class Seasons(override val items: List<JellyfinItem>) : JellyfinItems(items)

    data class Episodes(
        override val items: List<JellyfinItem>,
        val item: JellyfinItem,
    ) : JellyfinItems(items)
}
