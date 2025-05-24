package xyz.secozzi.jellyfinmanager.presentation.jellyfin

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import org.jellyfin.sdk.model.UUID
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.browse.JellyfinBrowseScreen
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreen
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreen
import xyz.secozzi.jellyfinmanager.presentation.ssh.components.PathLevelIndication
import xyz.secozzi.jellyfinmanager.presentation.utils.UIState
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinScreenViewModel.JellyfinItemList

@Composable
fun JellyfinScreenContent(
    state: UIState,
    items: Result<JellyfinItemList>,
    itemPath: List<Pair<String, UUID?>>,
    onNavigateTo: (Int) -> Unit,
    onClickItem: (JellyfinItem) -> Unit,
    onClickEditSeries: () -> Unit,
) {
    Column {
        PathLevelIndication(
            pathList = itemPath.map { it.first },
            onNavigateTo = onNavigateTo,
        )

        if (state.isWaiting()) {
            LoadingScreen()
            return@Column
        }

        if (state.isError()) {
            ErrorScreen(
                error = state.getError(),
            )
            return@Column
        }

        val jellyfinItems = items.getOrNull() ?: return@Column
        JellyfinBrowseScreen(
            jellyfinItems = jellyfinItems,
            onClickItem = onClickItem,
            onClickEditSeries = onClickEditSeries,
        )
    }
}
