package xyz.secozzi.jellyfinmanager.presentation.serverlist.server.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ServerTextField(
    value: String,
    label: String,
    required: Boolean,
    onValueChange: (String) -> Unit,
    validate: (String) -> Boolean = { it.isNotBlank() },
    errorMessage: String = "*required",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = if (required) !validate(value) else false,
        supportingText = {
            if (required) {
                if (!validate(value)) { Text(errorMessage) } else null
            } else {
                Text("(optional)")
            }
        },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}
