package xyz.secozzi.jellyfinmanager.presentation.jellyfin.episode.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun OutlinedNumericChooser(
    value: Int,
    onChange: (Int) -> Unit,
    max: Int,
    step: Int,
    modifier: Modifier = Modifier,
    min: Int = 0,
    isStart: Boolean? = null,
    isCrossing: Boolean? = null,
    suffix: (@Composable () -> Unit)? = null,
    label: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
    ) {
        var valueString by remember { mutableStateOf("$value") }
        LaunchedEffect(value) {
            if (valueString.isBlank() && value == 0) return@LaunchedEffect
            valueString = value.toString()
        }

        IconButton(
            onClick = { onChange(value - step) },
            enabled = enabled,
        ) {
            Icon(Icons.Filled.RemoveCircle, null)
        }

        OutlinedTextField(
            label = label,
            value = valueString,
            onValueChange = { newValue ->
                if (newValue.isBlank()) {
                    valueString = newValue
                    onChange(0)
                }
                runCatching {
                    val intValue = if (newValue.trimStart() == "-") -0 else newValue.toInt()
                    onChange(intValue)
                    valueString = newValue
                }
            },
            isError = value > max || value < min || isCrossing == true,
            supportingText = {
                if (isStart == true) {
                    if (isCrossing == true) {
                        Text(text = "Start cannot go above end")
                    } else if (value < min) {
                        Text(text = "Start cannot go below $min")
                    } else if (value > max) {
                        Text(text = "Start cannot go above $max")
                    }
                } else {
                    if (isCrossing == true) {
                        Text(text = "End cannot go below start")
                    } else if (value < min) {
                        Text(text = "End cannot go below $min")
                    } else if (value > max) {
                        Text(text = "End cannot go above $max")
                    }
                }
            },
            suffix = suffix,
            modifier = Modifier.weight(1f)
                .padding(top = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = enabled,
        )

        IconButton(
            onClick = { onChange(value + step) },
            enabled = enabled,
        ) {
            Icon(Icons.Filled.AddCircle, null)
        }
    }
}
