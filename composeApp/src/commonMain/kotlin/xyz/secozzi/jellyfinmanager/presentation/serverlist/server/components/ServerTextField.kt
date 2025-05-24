package xyz.secozzi.jellyfinmanager.presentation.serverlist.server.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun ServerTextField(
    value: String,
    label: String,
    required: Boolean,
    onValueChange: (String) -> Unit,
    validate: (String) -> Boolean = { it.isNotBlank() },
    errorMessage: String = "*required",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isPassword: Boolean = false,
) {
    val showPassword = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = if (required) !validate(value) else false,
        supportingText = {
            if (required) {
                if (!validate(value)) {
                    Text(errorMessage)
                } else {
                    null
                }
            } else {
                Text("(optional)")
            }
        },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (!isPassword ||
            showPassword.value
        ) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            if (isPassword) {
                val icon = if (showPassword.value) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }

                IconButton(onClick = { showPassword.value = !showPassword.value }) {
                    Icon(icon, contentDescription = "Visibility")
                }
            }
        },
    )
}
