package xyz.secozzi.jellyfinmanager.presentation.jellyfin.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.components.JellyfinIconItem
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.components.JellyfinImageItem
import xyz.secozzi.jellyfinmanager.presentation.utils.isLandscapeMode
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinScreenViewModel.JellyfinItemList
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun JellyfinBrowseScreen(
    jellyfinItems: JellyfinItemList,
    onClickItem: (JellyfinItem) -> Unit,
    onClickEditSeries: () -> Unit,
) {
    val aspectRatio = if (jellyfinItems is JellyfinItemList.Libraries) 16f / 9f else 2f / 3f

    val columns = if (isLandscapeMode()) {
        if (jellyfinItems is JellyfinItemList.Libraries) GridCells.FixedSize(300.dp) else GridCells.FixedSize(100.dp)
    } else {
        if (jellyfinItems is JellyfinItemList.Libraries) GridCells.Fixed(2) else GridCells.Fixed(3)
    }

    LazyVerticalGrid(
        columns = columns,
        contentPadding = PaddingValues(
            vertical = MaterialTheme.spacing.medium,
            horizontal = MaterialTheme.spacing.small,
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        if (jellyfinItems is JellyfinItemList.Seasons) {
            item {
                JellyfinIconItem(
                    name = "Edit series",
                    icon = Icons.Outlined.Edit,
                    imageAspectRatio = aspectRatio,
                    onClick = onClickEditSeries,
                )
            }
        }

        items(
            items = jellyfinItems.items,
            key = { it.id },
        ) { item ->
            JellyfinImageItem(
                name = item.name,
                imageUrl = item.image.primary?.toString(),
                imageAspectRatio = aspectRatio,
                onClick = { onClickItem(item) },
            )
        }
    }
}
