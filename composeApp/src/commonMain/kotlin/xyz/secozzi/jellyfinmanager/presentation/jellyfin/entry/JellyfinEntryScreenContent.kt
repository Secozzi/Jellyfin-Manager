package xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry.components.EditableDropdown
import xyz.secozzi.jellyfinmanager.presentation.theme.DISABLED_ALPHA
import xyz.secozzi.jellyfinmanager.presentation.utils.plus
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryScreenViewModel.JellyfinEntryDetails
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun JellyfinEntryScreenContent(
    details: JellyfinEntryDetails,
    onClickCopy: () -> Unit,
    onTitleChange: (String) -> Unit,
    onStudioChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onGenreChange: (String) -> Unit,
    onSeasonNumberChange: (String) -> Unit,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .padding(
                paddingValues + PaddingValues(
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.medium,
                ),
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClickCopy, modifier = Modifier.alpha(DISABLED_ALPHA)) {
                Icon(Icons.Outlined.ContentCopy, null)
            }

            Text(
                text = details.path,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = DISABLED_ALPHA),
            )
        }

        Text(
            text = details.providerIds.entries.joinToString(", ") { (k, v) ->
                "$k: $v"
            },
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = DISABLED_ALPHA),
        )

        EditableDropdown(
            value = details.title,
            label = "Title",
            values = details.titleList,
            onValueChange = onTitleChange,
        )

        OutlinedTextField(
            value = details.studio,
            onValueChange = onStudioChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Studio") },
            singleLine = true,
        )

        OutlinedTextField(
            value = details.description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Description") },
            minLines = 3,
        )

        OutlinedTextField(
            value = details.genre,
            onValueChange = onGenreChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Genre") },
            singleLine = true,
        )

        AnimatedVisibility(
            visible = details.seasonNumber != null,
        ) {
            OutlinedTextField(
                value = details.seasonNumber ?: "0",
                onValueChange = onSeasonNumberChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Season number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
    }
}
