package xyz.secozzi.jellyfinmanager.presentation.jellyfin.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSearchResult
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.components.JellyfinEntryItem
import xyz.secozzi.jellyfinmanager.presentation.utils.isLandscapeMode
import xyz.secozzi.jellyfinmanager.presentation.utils.plus
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun JellyfinSearchScreenContent(
    selectedId: String?,
    items: ImmutableList<JellyfinSearchResult>,
    onClickItem: (String?) -> Unit,
    paddingValues: PaddingValues = PaddingValues(),
) {
    val aspectRatio = 2f / 3f
    val columns = if (isLandscapeMode()) {
        GridCells.FixedSize(100.dp)
    } else {
        GridCells.Fixed(3)
    }

    LazyVerticalGrid(
        columns = columns,
        contentPadding = paddingValues + PaddingValues(
            vertical = MaterialTheme.spacing.medium,
            horizontal = MaterialTheme.spacing.small,
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        items(
            items = items,
            key = { it.hashCode() },
        ) { item ->
            JellyfinEntryItem(
                name = item.name,
                subtitle = item.year?.toString(),
                selected = item.id != null && item.id == selectedId,
                imageUrl = item.imageUrl,
                imageAspectRatio = aspectRatio,
                onClick = { onClickItem(item.id) },
            )
        }
    }
}
