package xyz.secozzi.jellyfinmanager.ui.home.tabs.jellyfin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.api.BaseItemKind
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.JellyfinScreenContent
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry.JellyfinEntryRoute
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry.JellyfinEntryRouteData
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreenViewModel

@Serializable
data object JellyfinRoute

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun JellyfinScreen() {
    val navigator = LocalNavController.current

    val homeViewModel = koinViewModel<HomeScreenViewModel>()
    val viewModel = koinViewModel<JellyfinTabViewModel>()

    LaunchedEffect(Unit) {
        homeViewModel.selectedServer.collect { selected ->
            selected?.let(viewModel::changeServer)
        }
    }

    val state by viewModel.state.collectAsState()
    val itemList by viewModel.itemList.collectAsState()

    BackHandler(itemList.size > 1) {
        viewModel.onNavigateTo(itemList.size - 2)
    }

    JellyfinScreenContent(
        state = state,
        itemList = itemList,
        onNavigateTo = viewModel::onNavigateTo,
        onClickItem = {
            when (it.type) {
                BaseItemKind.MOVIE -> navigator.navigate(JellyfinEntryRoute(JellyfinEntryRouteData(it.id)))
                BaseItemKind.SEASON -> navigator.navigate(JellyfinEntryRoute(JellyfinEntryRouteData(it.id)))
                else -> viewModel.onClickItem(it)
            }
        },
    )
}
