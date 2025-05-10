package xyz.secozzi.jellyfinmanager.ui.jellyfin

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemKind
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.domain.server.ServerStateHolder
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import xyz.secozzi.jellyfinmanager.presentation.utils.StateViewModel

class JellyfinScreenViewModel(
    private val jellyfinRepository: JellyfinRepository,
    private val serverStateHolder: ServerStateHolder,
) : StateViewModel<RequestState<JellyfinItems>>(RequestState.Idle) {
    private val _itemList = MutableStateFlow<List<Pair<String, UUID?>>>(listOf(Pair("Home", null)))
    val itemList = _itemList.asStateFlow()

    init {
        viewModelScope.launch {
            serverStateHolder.selectedServer.collect { selected ->
                selected?.let(::changeServer)
            }
        }
    }

    fun changeServer(server: Server?) {
        mutableState.update { _ -> RequestState.Loading }

        server?.let { s ->
            viewModelScope.launch {
                jellyfinRepository.loadServer(s)
                _itemList.update { _ -> listOf(Pair("Home", null)) }

                val libraries = jellyfinRepository.getLibraries()
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

        viewModelScope.launch {
            when (itemList.value.size) {
                1 -> {
                    val libraries = jellyfinRepository.getLibraries()
                    mutableState.update { _ ->
                        RequestState.Success(
                            JellyfinItems.Libraries(libraries)
                        )
                    }
                }
                else -> {
                    val currentItem = itemList.value.last()
                    val items = jellyfinRepository.getItems(currentItem.second)
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

        viewModelScope.launch {
            val items = jellyfinRepository.getItems(item.id)
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

@Serializable
enum class JellyfinItemType(name: String) {
    Season("Season"),
    Movie("Movie"),
    Series("Series"),
}
