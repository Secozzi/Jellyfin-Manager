package xyz.secozzi.jellyfinmanager.ui.jellyfin.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.serializer.UUIDSerializer
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSearchProvider
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.search.JellyfinSearchScreenContent
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.search.components.JellyfinSearchTopBar
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreen
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreen
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.presentation.utils.plus
import xyz.secozzi.jellyfinmanager.presentation.utils.serializableType
import xyz.secozzi.jellyfinmanager.ui.jellyfin.search.JellyfinSearchScreenViewModel.Companion.SEARCH_RESULT_KEY
import xyz.secozzi.jellyfinmanager.ui.theme.spacing
import xyz.secozzi.jellyfinmanager.utils.Platform
import xyz.secozzi.jellyfinmanager.utils.platform
import kotlin.reflect.typeOf

@Serializable
data class SearchRouteData(
    @Serializable(with = UUIDSerializer::class)
    val itemId: UUID,
    val searchProvider: JellyfinSearchProvider,
    val searchQuery: String,
)

@Serializable
data class SearchRoute(
    val data: SearchRouteData,
) {
    companion object {
        val typeMap = mapOf(
            typeOf<SearchRouteData>() to serializableType<SearchRouteData>(),
        )
    }
}

@Composable
fun JellyfinSearchScreen(searchQuery: String, searchProvider: JellyfinSearchProvider) {
    val navigator = LocalNavController.current
    val viewModel = koinViewModel<JellyfinSearchScreenViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    var selectedId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            JellyfinSearchTopBar(
                onBack = { navigator.popBackStack() },
                searchQuery = searchQuery,
                onSearch = { query ->
                    if (query.startsWith("id:")) {
                        navigator.popBackStack()
                        navigator.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set(SEARCH_RESULT_KEY, Pair(searchProvider, query.substringAfter("id:")))
                    } else {
                        viewModel.search(query)
                    }
                },
                focusRequester = focusRequester,
            )
        },
        bottomBar = {
            if (selectedId != null) {
                Button(
                    onClick = {
                        navigator.popBackStack()
                        navigator.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set(SEARCH_RESULT_KEY, Pair(searchProvider, selectedId))
                    },
                    enabled = state.isSuccess(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            WindowInsets.navigationBars.asPaddingValues() + PaddingValues(
                                start = MaterialTheme.spacing.medium,
                                end = MaterialTheme.spacing.medium,
                                bottom = if (platform == Platform.Desktop) MaterialTheme.spacing.small else 0.dp,
                            ),
                        ),
                ) {
                    Text("Select")
                }
            }
        },
    ) { contentPadding ->
        when {
            state.isWaiting() -> {
                LoadingScreen(contentPadding)
            }
            state.isError() -> {
                ErrorScreen(
                    error = state.getError(),
                    paddingValues = contentPadding,
                )
            }
            state.isSuccess() -> {
                val items = state.getData().toPersistentList()
                JellyfinSearchScreenContent(
                    selectedId = selectedId,
                    items = items,
                    onClickItem = { selectedId = it },
                    paddingValues = contentPadding,
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
