package xyz.secozzi.jellyfinmanager.ui.jellyfin.search

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSearchProvider
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSearchResult
import xyz.secozzi.jellyfinmanager.presentation.utils.StateViewModel
import xyz.secozzi.jellyfinmanager.presentation.utils.executeCatching

class JellyfinSearchScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val jellyfinRepository: JellyfinRepository,
) : StateViewModel() {
    val searchRoute = savedStateHandle.toRoute<SearchRoute>(
        typeMap = SearchRoute.typeMap,
    ).data

    private val _items = MutableStateFlow<List<JellyfinSearchResult>>(emptyList())
    val items = _items.asStateFlow()

    fun search(query: String) {
        executeCatching {
            val searchResult = jellyfinRepository.searchSeries(
                id = searchRoute.itemId,
                searchProvider = searchRoute.searchProvider.providerName,
                searchQuery = query,
            )
            _items.update { _ -> searchResult }
        }
    }

    companion object {
        const val SEARCH_RESULT_KEY = "search_result_key"
        typealias SEARCH_RESULT_TYPE = Pair<JellyfinSearchProvider, String>?
    }
}
