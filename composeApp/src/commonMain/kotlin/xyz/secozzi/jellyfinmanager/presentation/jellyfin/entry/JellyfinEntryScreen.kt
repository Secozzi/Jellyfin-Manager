package xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry.components.EditableDropdown
import xyz.secozzi.jellyfinmanager.presentation.theme.DISABLED_ALPHA
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinItemType
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryScreenViewModel.JellyfinEntryDetails
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun JellyfinEntryScreenContent(
    item: JellyfinEntryDetails,
    onTitleChange: (String) -> Unit,
    onStudioChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onGenreChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(
                start = MaterialTheme.spacing.medium,
                end = MaterialTheme.spacing.medium,
                bottom = MaterialTheme.spacing.medium,
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Text(
            text = item.path,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = DISABLED_ALPHA),
        )

        EditableDropdown(
            value = item.title,
            label = "Title",
            values = item.titleList,
            onValueChange = onTitleChange,
        )

        OutlinedTextField(
            value = item.studio,
            onValueChange = onStudioChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Studio") },
            singleLine = true,
        )

        OutlinedTextField(
            value = item.description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Description") },
            minLines = 3,
        )

        OutlinedTextField(
            value = item.genre,
            onValueChange = onGenreChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Genre") },
            singleLine = true,
        )
    }
}
