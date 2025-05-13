package xyz.secozzi.jellyfinmanager.presentation.jellyfin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jellyfin.sdk.model.UUID
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.browse.JellyfinBrowseScreen
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreenContent
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreenContent
import xyz.secozzi.jellyfinmanager.presentation.ssh.components.PathLevelIndication
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinScreenViewModel.JellyfinItemList

@Composable
fun JellyfinScreenContent(
    state: RequestState<JellyfinItemList>,
    itemList: List<Pair<String, UUID?>>,
    onNavigateTo: (Int) -> Unit,
    onClickItem: (JellyfinItem) -> Unit,
    onClickEditSeries: () -> Unit,
) {
    Column {
        PathLevelIndication(
            pathList = itemList.map { it.first },
            onNavigateTo = onNavigateTo,
        )

        if (state.isLoading() || state.isIdle()) {
            LoadingScreenContent()
            return@Column
        }

        if (state.isError()) {
            ErrorScreenContent(
                error = state.getError(),
                modifier = Modifier.fillMaxSize(),
            )
            return@Column
        }

        val jellyfinItems = state.getSuccessData()

        JellyfinBrowseScreen(
            jellyfinItems = jellyfinItems,
            onClickItem = onClickItem,
            onClickEditSeries = onClickEditSeries,
        )
    }
}
