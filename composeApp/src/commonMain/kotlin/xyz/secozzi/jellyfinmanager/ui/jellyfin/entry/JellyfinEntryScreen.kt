package xyz.secozzi.jellyfinmanager.ui.jellyfin.entry

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.ImageSearch
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.dokar.sonner.Toaster
import jellyfin_manager.composeapp.generated.resources.Res
import jellyfin_manager.composeapp.generated.resources.anidb
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.serializer.UUIDSerializer
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSearchProvider
import xyz.secozzi.jellyfinmanager.presentation.components.FABMenu
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry.JellyfinEntryScreenContent
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreen
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreen
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.presentation.utils.bottomBarPadding
import xyz.secozzi.jellyfinmanager.presentation.utils.serializableType
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinScreenViewModel.JellyfinItemType
import xyz.secozzi.jellyfinmanager.ui.jellyfin.cover.JellyfinCoverRoute
import xyz.secozzi.jellyfinmanager.ui.jellyfin.cover.JellyfinCoverRouteData
import xyz.secozzi.jellyfinmanager.ui.jellyfin.search.JellyfinSearchScreenViewModel.Companion.SEARCH_RESULT_KEY
import xyz.secozzi.jellyfinmanager.ui.jellyfin.search.JellyfinSearchScreenViewModel.Companion.SEARCH_RESULT_TYPE
import xyz.secozzi.jellyfinmanager.ui.jellyfin.search.SearchRoute
import xyz.secozzi.jellyfinmanager.ui.jellyfin.search.SearchRouteData
import xyz.secozzi.jellyfinmanager.ui.providers.LocalToaster
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
    val toaster = LocalToaster.current
    val viewModel = koinViewModel<JellyfinEntryScreenViewModel>()

    val state by viewModel.state.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val details by viewModel.details.collectAsState()
    val toasterEvent by viewModel.toasterEvent.collectAsStateWithLifecycle(null)

    val searchResult = backstack.savedStateHandle
        .getMutableStateFlow<SEARCH_RESULT_TYPE>(SEARCH_RESULT_KEY, null)
        .collectAsState()

    LaunchedEffect(searchResult) {
        searchResult.value?.let {
            viewModel.onSearch(it)
            backstack.savedStateHandle.set<SEARCH_RESULT_TYPE>(SEARCH_RESULT_KEY, null)
        }
    }

    LaunchedEffect(toasterEvent) {
        toasterEvent?.let { toaster.show(it) }
    }

    Toaster(state = toaster)

    val onSearchAnilist = remember {
        {
            navigator.navigate(
                SearchRoute(
                    SearchRouteData(
                        itemId = itemId,
                        searchProvider = JellyfinSearchProvider.Anilist,
                        searchQuery = details.title,
                    ),
                ),
            )
        }
    }

    val onSearchAniDB = remember {
        {
            navigator.navigate(
                SearchRoute(
                    SearchRouteData(
                        itemId = itemId,
                        searchProvider = JellyfinSearchProvider.AniDB,
                        searchQuery = details.title,
                    ),
                ),
            )
        }
    }

    val onClickCover = remember {
        {
            navigator.navigate(
                JellyfinCoverRoute(
                    JellyfinCoverRouteData(
                        itemId = itemId,
                    ),
                ),
            )
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
                        IconButton(onClick = onSearchAnilist) {
                            Icon(Icons.Outlined.Search, null)
                        }

                        IconButton(onClick = onSearchAniDB) {
                            Icon(vectorResource(Res.drawable.anidb), null)
                        }

                        IconButton(onClick = onClickCover) {
                            Icon(Icons.Outlined.ImageSearch, null)
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
                    .padding(bottomBarPadding()),
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
                var fabMenuExpanded by rememberSaveable { mutableStateOf(true) }

                FABMenu(
                    expanded = fabMenuExpanded,
                    onExpanded = { fabMenuExpanded = it },
                    onClickButton = {
                        when (it) {
                            0 -> onSearchAnilist
                            1 -> onSearchAniDB
                            2 -> onClickCover
                        }
                    },
                    buttons = listOf(
                        Icons.Outlined.Search to "Search details",
                        vectorResource(Res.drawable.anidb) to "Retrieve AniDB id",
                        Icons.Outlined.ImageSearch to "Select images",
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
