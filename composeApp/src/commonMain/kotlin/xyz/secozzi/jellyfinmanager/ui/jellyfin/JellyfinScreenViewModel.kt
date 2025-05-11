package xyz.secozzi.jellyfinmanager.ui.jellyfin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.UUID
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.domain.server.ServerStateHolder
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

typealias ItemList = List<Pair<String, UUID?>>

class JellyfinScreenViewModel(
    private val jellyfinRepository: JellyfinRepository,
    private val serverStateHolder: ServerStateHolder,
) : ViewModel() {
    private val hasInitializedServer = MutableStateFlow(false)
    private val serverFlow = MutableStateFlow<Server?>(null)
    private val refreshFlow = MutableSharedFlow<Unit>()

    private val _itemList = MutableStateFlow<ItemList>(listOf(Pair("Home", null)))
    val itemList = _itemList.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val state = combine(
        serverFlow.filterNotNull(),
        _itemList,
        refreshFlow.onStart { emit(Unit) },
    ) { server, itemList, _ ->
        server to itemList
    }
        .debounce(50.milliseconds)
        .flatMapLatest { (server, itemList) ->
            flow {
                emit(RequestState.Loading)

                if (!hasInitializedServer.value) {
                    jellyfinRepository.loadServer(server)
                    hasInitializedServer.update { _ -> true }
                }

                val items = getLibraries(itemList)
                emit(items)
            }
        }
        .catch { emit(RequestState.Error(it)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = RequestState.Idle,
        )

    init {
        viewModelScope.launch {
            serverStateHolder.selectedServer.filterNotNull().collectLatest { selected ->
                hasInitializedServer.update { _ -> false }
                _itemList.update { _ -> listOf(Pair("Home", null)) }

                serverFlow.update { _ -> selected }
            }
        }
    }

    private suspend fun getLibraries(itemList: ItemList): RequestState<JellyfinItems> {
        val items = when (itemList.size) {
            1 -> JellyfinItems.Libraries(jellyfinRepository.getLibraries())
            else -> {
                val items = jellyfinRepository.getItems(itemList.last().second)
                if (itemList.size == 2) {
                    JellyfinItems.Series(items)
                } else {
                    JellyfinItems.Seasons(items)
                }
            }
        }
        return RequestState.Success(items)
    }

    fun onNavigateTo(index: Int) {
        if (index == itemList.value.lastIndex) {
            return
        }

        _itemList.update { i -> i.subList(0, index + 1) }
    }

    fun onClickItem(item: JellyfinItem) {
        _itemList.update { i -> i + Pair(item.name, item.id) }
    }
}

sealed class JellyfinItems(open val items: List<JellyfinItem>) {
    data class Libraries(override val items: List<JellyfinItem>) : JellyfinItems(items)
    data class Series(override val items: List<JellyfinItem>) : JellyfinItems(items)
    data class Seasons(override val items: List<JellyfinItem>) : JellyfinItems(items)
}

@Serializable
enum class JellyfinItemType(name: String) {
    Season("Season"),
    Movie("Movie"),
    Series("Series"),
}
