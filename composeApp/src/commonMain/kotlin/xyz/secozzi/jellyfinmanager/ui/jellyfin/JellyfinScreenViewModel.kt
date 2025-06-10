package xyz.secozzi.jellyfinmanager.ui.jellyfin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemKind
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.domain.server.ServerStateHolder
import xyz.secozzi.jellyfinmanager.presentation.utils.combineRefreshable

typealias ItemPath = List<Pair<String, UUID?>>

class JellyfinScreenViewModel(
    private val jellyfinRepository: JellyfinRepository,
    private val serverStateHolder: ServerStateHolder,
) : ViewModel() {
    private val hasInitializedServer = MutableStateFlow(false)
    private val refreshFlow = MutableSharedFlow<Unit>()

    private val _jfData = MutableStateFlow<JellyfinVMData>(JellyfinVMData.EMPTY)
    val jfData = _jfData.asStateFlow()

    val state = combineRefreshable(
        jfData.filter { it.server != null },
        refreshFlow,
    ) { jfData ->
        if (!hasInitializedServer.value) {
            jellyfinRepository.loadServer(jfData.server!!)
            hasInitializedServer.update { _ -> true }
        }

        getLibraries(jfData.itemPath)
    }

    init {
        viewModelScope.launch {
            serverStateHolder.selectedServer.filterNotNull().collectLatest { selected ->
                hasInitializedServer.update { _ -> false }

                _jfData.update { _ ->
                    JellyfinVMData(
                        server = selected,
                        itemPath = listOf(Pair("Home", null)),
                    )
                }
            }
        }
    }

    private suspend fun getLibraries(itemPath: ItemPath): JellyfinItemList {
        return when (itemPath.size) {
            1 -> JellyfinItemList.Libraries(jellyfinRepository.getLibraries())
            else -> {
                val items = jellyfinRepository.getItems(itemPath.last().second)
                if (itemPath.size == 2) {
                    JellyfinItemList.Series(items)
                } else {
                    JellyfinItemList.Seasons(items, itemPath.last().second!!)
                }
            }
        }
    }

    fun onNavigateTo(index: Int) {
        if (index == jfData.value.itemPath.lastIndex) {
            return
        }

        _jfData.update { data ->
            data.copy(
                itemPath = data.itemPath.subList(0, index + 1),
            )
        }
    }

    fun onClickItem(item: JellyfinItem) {
        _jfData.update { data ->
            data.copy(
                itemPath = data.itemPath + Pair(item.name, item.id),
            )
        }
    }

    data class JellyfinVMData(
        val server: Server?,
        val itemPath: ItemPath,
    ) {
        companion object {
            val EMPTY = JellyfinVMData(
                server = null,
                itemPath = emptyList(),
            )
        }
    }

    sealed class JellyfinItemList(open val items: List<JellyfinItem>) {
        data class Libraries(override val items: List<JellyfinItem>) : JellyfinItemList(items)
        data class Series(override val items: List<JellyfinItem>) : JellyfinItemList(items)
        data class Seasons(
            override val items: List<JellyfinItem>,
            val seriesId: UUID,
        ) : JellyfinItemList(items)
    }

    @Serializable
    enum class JellyfinItemType(name: String, val type: BaseItemKind) {
        Season("Season", BaseItemKind.SEASON),
        Movie("Movie", BaseItemKind.MOVIE),
        Series("Series", BaseItemKind.SERIES),
    }
}
