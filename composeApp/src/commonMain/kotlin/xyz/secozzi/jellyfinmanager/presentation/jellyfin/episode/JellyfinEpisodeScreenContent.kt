package xyz.secozzi.jellyfinmanager.presentation.jellyfin.episode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import xyz.secozzi.jellyfinmanager.domain.anidb.models.AniDBEpisodeType
import xyz.secozzi.jellyfinmanager.domain.ssh.model.Directory
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.components.DropdownMenu
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.episode.components.EpisodePreviewCard
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.episode.components.OutlinedNumericChooser
import xyz.secozzi.jellyfinmanager.presentation.theme.DISABLED_ALPHA
import xyz.secozzi.jellyfinmanager.ui.jellyfin.episode.JellyfinEpisodeScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun JellyfinEpisodeScreenContent(
    aniDBId: Long?,
    selectedType: AniDBEpisodeType,
    availableTypes: ImmutableList<AniDBEpisodeType>,
    episodeInfo: JellyfinEpisodeScreenViewModel.EpisodeInfo,
    remoteFileList: ImmutableList<Directory>,
    updateStart: (Int) -> Unit,
    updateEnd: (Int) -> Unit,
    updateOffset: (Int) -> Unit,
    onSelectType: (AniDBEpisodeType) -> Unit,
    paddingValues: PaddingValues,
) {
    if (aniDBId == null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Outlined.Warning, null)
            Text("No AniDB id available for selected entry.")
        }
    } else {
        Column(
            modifier = Modifier.padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                ),
        ) {
            DropdownMenu(
                label = "Type",
                selectedItem = selectedType,
                items = availableTypes,
                modifier = Modifier.fillMaxWidth(),
                onSelect = onSelectType,
            )

            OutlinedNumericChooser(
                value = episodeInfo.start,
                onChange = updateStart,
                max = selectedType.extraData!!,
                step = 1,
                min = 1,
                label = { Text(text = "Start") },
                isStart = true,
                isCrossing = episodeInfo.start > episodeInfo.end,
            )

            OutlinedNumericChooser(
                value = episodeInfo.end,
                onChange = updateEnd,
                max = selectedType.extraData!!,
                step = 1,
                min = 1,
                label = { Text(text = "End") },
                isStart = false,
                isCrossing = episodeInfo.start > episodeInfo.end,
            )

            OutlinedNumericChooser(
                value = episodeInfo.offset,
                onChange = updateOffset,
                max = Int.MAX_VALUE,
                step = 1,
                min = Int.MIN_VALUE,
                label = { Text(text = "Offset") },
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                HorizontalDivider()

                if (remoteFileList.isNotEmpty()) {
                    Text(remoteFileList.first().name.substringAfterLast("/"), modifier = Modifier.alpha(DISABLED_ALPHA))
                }

                if (remoteFileList.size > 1) {
                    Text(remoteFileList.last().name.substringAfterLast("/"), modifier = Modifier.alpha(DISABLED_ALPHA))
                }

                Row {
                    Icon(
                        Icons.Outlined.Numbers,
                        null,
                        modifier = Modifier.padding(start = 14.dp),
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.smaller))
                    Text(text = "Video count")
                    Spacer(modifier = Modifier.weight(1.0f))
                    Text(
                        text = (episodeInfo.end - episodeInfo.start + 1).toString(),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 14.dp),
                    )
                }

                val previewModifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(MaterialTheme.spacing.medium)

                episodeInfo.startPreview?.let {
                    EpisodePreviewCard(
                        title = it.englishTitle ?: it.romajiTitle ?: it.nativeTitle ?: "",
                        episodeNumber = it.episodeNumber,
                        originalEpisodeNumber = it.episodeNumber - episodeInfo.offset,
                        extraInfo = listOfNotNull(it.airingDate),
                        modifier = previewModifier,
                    )
                }

                episodeInfo.endPreview?.let {
                    EpisodePreviewCard(
                        title = it.englishTitle ?: it.romajiTitle ?: it.nativeTitle ?: "",
                        episodeNumber = it.episodeNumber,
                        originalEpisodeNumber = it.episodeNumber - episodeInfo.offset,
                        extraInfo = listOfNotNull(it.airingDate),
                        modifier = previewModifier,
                    )
                }
            }
        }
    }
}
