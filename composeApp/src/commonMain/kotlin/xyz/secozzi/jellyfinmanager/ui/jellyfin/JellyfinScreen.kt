package xyz.secozzi.jellyfinmanager.ui.jellyfin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.api.BaseItemKind
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.JellyfinScreenContent
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryRoute
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryRouteData

@Serializable
data object JellyfinRoute

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun JellyfinScreen() {
    val navigator = LocalNavController.current
    val viewModel = koinViewModel<JellyfinScreenViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val jfData by viewModel.jfData.collectAsState()

    BackHandler(jfData.itemList.size > 1) {
        viewModel.onNavigateTo(jfData.itemList.size - 2)
    }

    JellyfinScreenContent(
        state = state,
        itemList = jfData.itemList,
        onNavigateTo = viewModel::onNavigateTo,
        onClickItem = {
            when (it.type) {
                BaseItemKind.MOVIE -> navigator.navigate(
                    route = JellyfinEntryRoute(
                        data = JellyfinEntryRouteData(
                            itemId = it.id,
                            itemType = JellyfinItemType.Movie,
                        )
                    )
                )
                BaseItemKind.SEASON -> navigator.navigate(
                    route = JellyfinEntryRoute(
                        data = JellyfinEntryRouteData(
                            itemId = it.id,
                            itemType = JellyfinItemType.Season,
                        )
                    )
                )
                else -> viewModel.onClickItem(it)
            }
        },
    )
}
