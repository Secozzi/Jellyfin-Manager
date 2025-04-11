package xyz.secozzi.jellyfinmanager.presentation.serverlist.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ServerListDeleteDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    title: String,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onDelete()
                onDismissRequest()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        title = {
            Text("Delete server")
        },
        text = {
            Text("Do you wish to delete the server \"$title\"?")
        },
    )
}
