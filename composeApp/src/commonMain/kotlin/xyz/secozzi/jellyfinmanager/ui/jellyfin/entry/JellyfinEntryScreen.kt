package xyz.secozzi.jellyfinmanager.ui.jellyfin.entry

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.serializer.UUIDSerializer
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.presentation.components.FabButtonItem
import xyz.secozzi.jellyfinmanager.presentation.components.FabButtonMain
import xyz.secozzi.jellyfinmanager.presentation.components.FabButtonState
import xyz.secozzi.jellyfinmanager.presentation.components.MultiFloatingActionButton
import xyz.secozzi.jellyfinmanager.presentation.components.rememberMultiFabState
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry.JellyfinEntryScreenContent
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreen
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreen
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.presentation.utils.serializableType
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinScreenViewModel.JellyfinItemType
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryScreenViewModel.Companion.SEARCH_RESULT_KEY
import xyz.secozzi.jellyfinmanager.ui.jellyfin.search.SearchRoute
import xyz.secozzi.jellyfinmanager.ui.jellyfin.search.SearchRouteData
import xyz.secozzi.jellyfinmanager.ui.theme.spacing
import xyz.secozzi.jellyfinmanager.utils.Platform
import xyz.secozzi.jellyfinmanager.utils.platform
import kotlin.reflect.typeOf

@Serializable
data class JellyfinEntryRouteData(
    @Serializable(with = UUIDSerializer::class)
    val itemId: UUID,
    val itemType: JellyfinItemType,
)

@Serializable
data class JellyfinEntryRoute(
    val data: JellyfinEntryRouteData,
) {
    companion object {
        val typeMap = mapOf(
            typeOf<JellyfinEntryRouteData>() to serializableType<JellyfinEntryRouteData>(),
            typeOf<JellyfinItemType>() to serializableType<JellyfinItemType>(),
        )
    }
}

@Composable
fun JellyfinEntryScreen(
    backstack: NavBackStackEntry,
    type: JellyfinItemType,
    itemId: UUID,
) {
    val navigator = LocalNavController.current
    val viewModel = koinViewModel<JellyfinEntryScreenViewModel>()

    val state by viewModel.state.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val details by viewModel.details.collectAsState()

    val searchResult = backstack.savedStateHandle
        .getMutableStateFlow<String?>(SEARCH_RESULT_KEY, null)
        .collectAsState()

    LaunchedEffect(searchResult) {
        searchResult.value?.let {
            viewModel.onSearch(it)
            backstack.savedStateHandle.set<String?>(SEARCH_RESULT_KEY, null)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Manage ${type.name.lowercase()}")
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
                actions = {
                    if (platform == Platform.Desktop) {
                        IconButton(onClick = {
                            navigator.navigate(SearchRoute(SearchRouteData(itemId, details.title)))
                        }) {
                            Icon(Icons.Outlined.Search, null)
                        }

                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Refresh, null)
                        }
                    }
                },
            )
        },
        bottomBar = {
            Button(
                onClick = viewModel::save,
                enabled = state.isSuccess(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .padding(horizontal = MaterialTheme.spacing.medium)
                    .padding(bottom = if (platform == Platform.Desktop) MaterialTheme.spacing.small else 0.dp),
            ) {
                when (saveState) {
                    JellyfinEntryScreenViewModel.SaveState.Idle -> {
                        Text("Update ${type.name.lowercase()}")
                    }
                    JellyfinEntryScreenViewModel.SaveState.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.then(Modifier.size(24.dp)),
                            strokeWidth = 2.dp,
                        )
                    }
                    JellyfinEntryScreenViewModel.SaveState.Error -> {
                        Icon(Icons.Default.Error, null)
                    }
                    JellyfinEntryScreenViewModel.SaveState.Success -> {
                        Icon(Icons.Default.Check, null)
                    }
                }
            }
        },
        floatingActionButton = {
            if (platform == Platform.Android) {
                val fabState = rememberMultiFabState()

                MultiFloatingActionButton(
                    items = listOf(
                        FabButtonItem(
                            iconRes = Icons.Default.CreateNewFolder,
                            label = "New folder",
                            key = "add",
                        ),
                        FabButtonItem(
                            iconRes = Icons.Default.Delete,
                            label = "Delete current",
                            key = "delete",
                        ),
                    ),
                    fabState = fabState,
                    onFabItemClicked = { btn ->
                        navigator.navigate(SearchRoute)
                        fabState.value = FabButtonState.Collapsed
                    },
                    fabIcon = FabButtonMain(
                        iconRes = Icons.Filled.Add,
                        iconRotate = 45f,
                    ),
                )
            }
        },
    ) { contentPadding ->
        if (state.isWaiting()) {
            LoadingScreen(contentPadding)
            return@Scaffold
        }

        if (state.isError()) {
            ErrorScreen(
                error = state.getError(),
                paddingValues = contentPadding,
            )
            return@Scaffold
        }

        JellyfinEntryScreenContent(
            details = details,
            onTitleChange = viewModel::onTitleChange,
            onStudioChange = viewModel::onStudioChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onGenreChange = viewModel::onGenreChange,
            onSeasonNumberChange = viewModel::onSeasonNumberChange,
            paddingValues = contentPadding,
        )
    }
}
