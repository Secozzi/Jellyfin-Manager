package xyz.secozzi.jellyfinmanager.presentation.jellyfin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.size.Size
import org.jetbrains.compose.ui.tooling.preview.Preview
import xyz.secozzi.jellyfinmanager.presentation.theme.SECONDARY_ALPHA
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun JellyfinEntryItem(
    name: String,
    imageUrl: String?,
    subtitle: String? = null,
    selected: Boolean = false,
    imageAspectRatio: Float,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.extraSmall)
            .selectedOutline(
                isSelected = selected,
                color = MaterialTheme.colorScheme.secondary,
            )
            .padding(4.dp)
            .combinedClickable(
                onClick = onClick,
            ),
    ) {
        val contentColor = if (selected) {
            MaterialTheme.colorScheme.onSecondary
        } else {
            LocalContentColor.current
        }

        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(imageUrl)
                        .size(Size.ORIGINAL)
                        .build(),
                    placeholder = ColorPainter(Color(0x1F888888)),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(imageAspectRatio)
                        .clip(MaterialTheme.shapes.extraSmall),
                    contentScale = ContentScale.Crop,
                )

                Text(
                    text = name,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    minLines = 1,
                    maxLines = 3,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = MaterialTheme.spacing.extraSmall,
                            end = MaterialTheme.spacing.extraSmall,
                            top = MaterialTheme.spacing.extraSmall,
                        ),
                )

                subtitle?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        minLines = 1,
                        maxLines = 3,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .alpha(SECONDARY_ALPHA)
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.extraSmall),
                    )
                }
            }

            if (selected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(MaterialTheme.spacing.small),
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

private fun Modifier.selectedOutline(
    isSelected: Boolean,
    color: Color,
) = drawBehind { if (isSelected) drawRect(color = color) }

@Composable
fun EditJellyfinItem(
    imageAspectRatio: Float,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraSmall)
            .padding(4.dp)
            .combinedClickable(
                onClick = onClick,
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .fillMaxSize()
                    .aspectRatio(imageAspectRatio)
                    .clip(MaterialTheme.shapes.extraSmall),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Edit, null)
            }

            Text(
                text = "Edit series",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                minLines = 1,
                maxLines = 3,
                style = MaterialTheme.typography.titleSmall,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.extraSmall),
            )
        }
    }
}

@Preview
@Composable
fun JellyfinEntryItemPreview() {
    JellyfinEntryItem(
        name = "One Punch Man",
        imageUrl = "https://meo.comick.pictures/mndnY6-m.jpg",
        imageAspectRatio = 2f / 3f,
        onClick = {},
    )
}
