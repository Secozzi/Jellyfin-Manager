package xyz.secozzi.jellyfinmanager.ui.jellyfin.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSearchProvider
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSearchResult
import xyz.secozzi.jellyfinmanager.presentation.utils.StateViewModel
import xyz.secozzi.jellyfinmanager.presentation.utils.UIState

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
        mutableState.update { _ -> UIState.Loading }
        viewModelScope.launch {
            val searchResult = jellyfinRepository.searchSeries(
                id = searchRoute.itemId,
                searchProvider = JellyfinSearchProvider.AniList.providerName,
                searchQuery = query,
            )
            _items.update { _ -> searchResult }
            mutableState.update { _ -> UIState.Success }
        }
    }
}
