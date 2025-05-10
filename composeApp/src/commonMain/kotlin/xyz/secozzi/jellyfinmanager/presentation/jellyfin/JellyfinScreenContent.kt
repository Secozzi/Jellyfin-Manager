package xyz.secozzi.jellyfinmanager.presentation.jellyfin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jellyfin.sdk.model.UUID
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.browse.JellyfinBrowseScreen
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreenContent
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreenContent
import xyz.secozzi.jellyfinmanager.presentation.ssh.components.PathLevelIndication
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinItems

@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun JellyfinScreenContent(
    state: RequestState<JellyfinItems>,
    itemList: List<Pair<String, UUID?>>,
    onNavigateTo: (Int) -> Unit,
    onClickItem: (JellyfinItem) -> Unit,
) {
    Scaffold(
        floatingActionButton = {},
    ) {
        Column {
            PathLevelIndication(
                pathList = itemList.map { it.first },
                onNavigateTo = onNavigateTo,
            )

            if (state.isLoading() || state.isIdle()) {
                LoadingScreenContent()
                return@Scaffold
            }

            if (state.isError()) {
                ErrorScreenContent(
                    error = state.getError(),
                    modifier = Modifier.fillMaxSize(),
                )
                return@Scaffold
            }

            val jellyfinItems = state.getSuccessData()

            JellyfinBrowseScreen(
                jellyfinItems = jellyfinItems,
                onClickItem = onClickItem,
            )
        }
    }
}
