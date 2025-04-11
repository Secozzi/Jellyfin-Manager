package xyz.secozzi.jellyfinmanager.presentation.serverlist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun ServerListItem(
    server: Server,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEdit() }
                .padding(
                    start = MaterialTheme.spacing.medium,
                    top = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Cloud, null)
            Text(
                text = server.name,
                modifier = Modifier
                    .padding(start = MaterialTheme.spacing.medium),
            )
        }
        Row {
            IconButton(
                onClick = onMoveUp,
                enabled = canMoveUp,
            ) {
                Icon(imageVector = Icons.Outlined.ArrowDropUp, contentDescription = null)
            }
            IconButton(
                onClick = onMoveDown,
                enabled = canMoveDown,
            ) {
                Icon(imageVector = Icons.Outlined.ArrowDropDown, contentDescription = null)
            }
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onEdit) {
                Icon(Icons.Outlined.Edit, null)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, null)
            }
        }
    }
}
