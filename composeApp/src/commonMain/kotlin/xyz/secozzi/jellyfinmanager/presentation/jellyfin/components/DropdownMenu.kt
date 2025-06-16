package xyz.secozzi.jellyfinmanager.presentation.jellyfin.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import xyz.secozzi.jellyfinmanager.presentation.theme.DISABLED_ALPHA
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

interface DropdownItem {
    val displayName: String
    val id: Int
    val extraData: Int?
}

internal data class DropdownItemClass(
    override val displayName: String,
    override val id: Int,
    override val extraData: Int? = null,
) : DropdownItem

@Composable
fun <T> DropdownMenu(
    label: String,
    selectedItem: T,
    items: List<T>,
    onSelect: (T) -> Unit,
    displayItem: (T) -> String,
    modifier: Modifier = Modifier,
) {
    val dropdownItems = items.mapIndexed { idx, item ->
        DropdownItemClass(
            displayName = displayItem(item),
            id = idx,
        )
    }

    val selectedDropdownItem = DropdownItemClass(
        displayName = displayItem(selectedItem),
        id = 0,
    )

    DropdownMenu(
        label = label,
        selectedItem = selectedDropdownItem,
        items = dropdownItems,
        onSelect = { onSelect(items[it.id]) },
        modifier = modifier,
    )
}

@Composable
fun <T : DropdownItem> DropdownMenu(
    label: String,
    selectedItem: T,
    items: List<T>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            readOnly = true,
            value = selectedItem.displayName,
            onValueChange = {},
            label = { Text(text = label) },
            suffix = {
                selectedItem.extraData?.let {
                    Text(
                        text = "($it)",
                        modifier = Modifier.alpha(DISABLED_ALPHA),
                    )
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
                        ) {
                            Text(text = item.displayName)
                            item.extraData?.let {
                                Text(
                                    text = "($it)",
                                    modifier = Modifier.alpha(DISABLED_ALPHA),
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    },
                )
            }
        }
    }
}
