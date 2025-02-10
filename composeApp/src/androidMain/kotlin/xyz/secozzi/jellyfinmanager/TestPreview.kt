package xyz.secozzi.jellyfinmanager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun TestPreview() {
    val interactionSource = remember { MutableInteractionSource() }
    var expanded by remember { mutableStateOf(false) }

    val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                        expanded = !expanded
                    },
                    interactionSource = interactionSource,
                    indication = null,
                )
        ) {
            Text("Anime")
            Icon(icon, contentDescription = "More options")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Option 1") },
                onClick = { /* Do something... */ }
            )
            DropdownMenuItem(
                text = { Text("Option 2") },
                onClick = { /* Do something... */ }
            )
        }
    }
}