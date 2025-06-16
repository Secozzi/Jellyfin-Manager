package xyz.secozzi.jellyfinmanager.presentation.jellyfin.episode.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import xyz.secozzi.jellyfinmanager.presentation.theme.DISABLED_ALPHA

@Composable
fun EpisodePreviewCard(
    title: String,
    episodeNumber: Int,
    originalEpisodeNumber: Int? = null,
    extraInfo: List<String>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Row {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Row {
                    Text(
                        text = buildList {
                            if (episodeNumber != 0) {
                                add("Episode $episodeNumber")
                            }
                            addAll(extraInfo)
                        }
                            .filter { it.isNotEmpty() }
                            .joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            originalEpisodeNumber?.let {
                Text(
                    text = "($it)",
                    modifier = Modifier.alpha(DISABLED_ALPHA),
                )
            }
        }
    }
}
