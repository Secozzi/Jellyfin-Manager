package xyz.secozzi.jellyfinmanager.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import xyz.secozzi.jellyfinmanager.presentation.LoadingScreenContent
import xyz.secozzi.jellyfinmanager.ui.theme.spacing


enum class Dialogs {
    Confirm, EditText,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDialog(
    title: String,
    subtitle: String,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onCancel,
        modifier = modifier,
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = AlertDialogDefaults.titleContentColor,
                )

                if (isLoading) {
                    LoadingScreenContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.medium),
                    )
                    return@Column
                }

                Text(
                    subtitle,
                    color = AlertDialogDefaults.textContentColor,
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onCancel) {
                        Text("Cancel")
                    }
                    TextButton(onConfirm) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextDialog(
    title: String,
    isLoading: Boolean,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf("") }

    BasicAlertDialog(
        onCancel,
        modifier = modifier,
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = AlertDialogDefaults.titleContentColor,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (isLoading) {
                    LoadingScreenContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.medium),
                    )
                    return@Column
                }

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    // colors = AlertDialogDefaults.titleContentColor,
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onCancel) {
                        Text("Cancel")
                    }
                    TextButton(onClick = { onConfirm(text) }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}