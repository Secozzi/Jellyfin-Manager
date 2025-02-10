package xyz.secozzi.jellyfinmanager.presentation.preferences

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsMenuLink

@Composable
fun <T> BasicTextFieldPreference(
    value: T,
    onValueChange: (T) -> Unit,
    title: @Composable () -> Unit,
    textToValue: (String) -> T?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    summary: @Composable (() -> Unit)? = null,
    valueToText: (T) -> String = { it.toString() },
    isValid: (String) -> Boolean = { true },
    textField: @Composable (
        value: TextFieldValue,
        onValueChange: (TextFieldValue) -> Unit,
        onOk: () -> Unit,
    ) -> Unit,
) {
    var openDialog by rememberSaveable { mutableStateOf(false) }

    SettingsMenuLink(
        title = title,
        subtitle = summary,
        onClick = { openDialog = true },
        enabled = enabled,
        modifier = modifier,
    )

    if (openDialog) {
        var dialogText by
        rememberSaveable(stateSaver = TextFieldValue.Saver) {
            val text = valueToText(value)
            mutableStateOf(TextFieldValue(text, TextRange(text.length)))
        }
        val onOk = {
            val dialogValue = textToValue(dialogText.text)
            if (dialogValue != null) {
                onValueChange(dialogValue)
                openDialog = false
            }
        }
        PreferenceAlertDialog(
            onDismissRequest = { openDialog = false },
            title = title,
            buttons = {
                TextButton(onClick = { openDialog = false }) {
                    Text(text = "Cancel")
                }
                TextButton(
                    onClick = onOk,
                    enabled = isValid(dialogText.text)
                ) { Text(text = "OK") }
            },
        ) {
            val focusRequester = remember { FocusRequester() }
            Box(
                modifier =
                Modifier.fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .focusRequester(focusRequester)
            ) {
                textField(dialogText, { dialogText = it }, onOk)
            }
            LaunchedEffect(focusRequester) { focusRequester.requestFocus() }
        }
    }
}

@Composable
fun <T> TextFieldPreference(
    value: T,
    onValueChange: (T) -> Unit,
    title: @Composable () -> Unit,
    textToValue: (String) -> T?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    summary: @Composable (() -> Unit)? = null,
    valueToText: (T) -> String = { it.toString() },
    isValid: (String) -> Boolean = { true },
    errorText: @Composable () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    BasicTextFieldPreference(
        value = value,
        onValueChange = onValueChange,
        title = title,
        textToValue = textToValue,
        modifier = modifier,
        enabled = enabled,
        summary = summary,
        valueToText = valueToText,
        isValid = isValid,
    ) { dialogValue, dialogOnValueChange, dialogOnOk ->
        OutlinedTextField(
            value = dialogValue,
            onValueChange = dialogOnValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions { dialogOnOk() },
            singleLine = true,
            isError = !isValid(dialogValue.text),
            supportingText = {
                if (!isValid(dialogValue.text)) {
                    errorText()
                }
            },
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
        )
    }
}

@Composable
fun <T> PasswordPreference(
    value: T,
    onValueChange: (T) -> Unit,
    title: @Composable () -> Unit,
    textToValue: (String) -> T?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueToText: (T) -> String = { it.toString() },
) {
    BasicTextFieldPreference(
        value = value,
        onValueChange = onValueChange,
        title = title,
        textToValue = textToValue,
        modifier = modifier,
        enabled = enabled,
        summary = { Text(text = if ( valueToText(value).isBlank() ) "" else "●●●●●●●●") },
        valueToText = valueToText,
        isValid = { true },
    ) { dialogValue, dialogOnValueChange, dialogOnOk ->
        val showPassword = remember { mutableStateOf(false) }

        OutlinedTextField(
            value = dialogValue,
            onValueChange = dialogOnValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions { dialogOnOk() },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (showPassword.value) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }

                IconButton(onClick = { showPassword.value = !showPassword.value }) {
                    Icon(icon, contentDescription = "Visibility")
                }
            }
        )
    }
}
