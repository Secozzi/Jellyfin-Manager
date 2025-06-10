package xyz.secozzi.jellyfinmanager.presentation.jellyfin.cover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinImageInfo
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinImageType
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.components.DropdownMenu
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.components.JellyfinIconItem
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.components.JellyfinImageItem
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreen
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreen
import xyz.secozzi.jellyfinmanager.presentation.utils.UiState
import xyz.secozzi.jellyfinmanager.presentation.utils.isLandscapeMode
import xyz.secozzi.jellyfinmanager.presentation.utils.plus
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun JellyfinCoverScreenContent(
    state: UiState<List<JellyfinImageInfo>>,
    selectedType: JellyfinImageType,
    current: UiState<Map<JellyfinImageType, String>>,
    selectedImage: Int?,
    onSelectImage: (Int) -> Unit,
    onSelect: (JellyfinImageType) -> Unit,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                paddingValues + PaddingValues(
                    horizontal = MaterialTheme.spacing.medium,
                ),
            ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        DropdownMenu(
            label = "Type",
            selectedItem = selectedType,
            items = JellyfinImageType.entries.toList(),
            onSelect = onSelect,
            displayItem = { it.name },
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.isWaiting()) {
            LoadingScreen(paddingValues)
            return@Column
        }

        if (state.isError()) {
            ErrorScreen(
                error = state.getError(),
                paddingValues = paddingValues,
            )
            return@Column
        }

        val aspectRatio = if (selectedType == JellyfinImageType.Primary) 2f / 3f else 16f / 9f
        val columns = if (isLandscapeMode()) {
            if (selectedType == JellyfinImageType.Primary) GridCells.FixedSize(100.dp) else GridCells.FixedSize(300.dp)
        } else {
            if (selectedType == JellyfinImageType.Primary) GridCells.Fixed(3) else GridCells.Fixed(2)
        }

        val images = state.getData()
        val current = current.getDataOrNull() ?: mapOf()
        LazyVerticalGrid(
            columns = columns,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            item {
                current[selectedType]?.let {
                    JellyfinImageItem(
                        name = "Current",
                        selected = selectedImage == 0,
                        imageUrl = it,
                        imageAspectRatio = aspectRatio,
                        onClick = { onSelectImage(0) },
                    )
                } ?: JellyfinIconItem(
                    name = "Current",
                    selected = selectedImage == 0,
                    icon = Icons.Outlined.BrokenImage,
                    imageAspectRatio = aspectRatio,
                    onClick = { onSelectImage(0) },
                )
            }

            itemsIndexed(
                items = images,
                key = { index, _ -> index },
            ) { index, item ->
                JellyfinImageItem(
                    name = item.provider,
                    subtitle = item.extraInfo,
                    selected = selectedImage == index + 1,
                    imageUrl = item.url,
                    imageAspectRatio = aspectRatio,
                    onClick = { onSelectImage(index + 1) },
                )
            }
        }
    }
}
