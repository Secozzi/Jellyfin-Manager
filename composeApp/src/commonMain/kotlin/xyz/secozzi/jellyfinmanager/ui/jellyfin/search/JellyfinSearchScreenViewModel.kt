package xyz.secozzi.jellyfinmanager.ui.jellyfin.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSearchProvider
import xyz.secozzi.jellyfinmanager.presentation.utils.asResultFlow

class JellyfinSearchScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val jellyfinRepository: JellyfinRepository,
) : ViewModel() {
    val searchRoute = savedStateHandle.toRoute<SearchRoute>(
        typeMap = SearchRoute.typeMap,
    ).data

    private val searchFlow = MutableStateFlow("")
    val state = searchFlow.asResultFlow {
        jellyfinRepository.searchSeries(
            id = searchRoute.itemId,
            searchProvider = searchRoute.searchProvider.providerName,
            searchQuery = it,
        )
    }

    fun search(query: String) {
        searchFlow.update { _ -> query }
    }

    companion object {
        const val SEARCH_RESULT_KEY = "search_result_key"
        typealias SEARCH_RESULT_TYPE = Pair<JellyfinSearchProvider, String>?
    }
}
