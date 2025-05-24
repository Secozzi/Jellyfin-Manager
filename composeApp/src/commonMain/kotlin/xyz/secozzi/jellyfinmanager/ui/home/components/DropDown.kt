package xyz.secozzi.jellyfinmanager.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import xyz.secozzi.jellyfinmanager.domain.database.models.Server

@Composable
fun DropDown(
    server: Server,
    values: ImmutableList<Server>,
    onSelect: (Server) -> Unit,
) {
    val isDropDownExpanded = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    isDropDownExpanded.value = !isDropDownExpanded.value
                },
            ) {
                Text(server.name)
                Icon(Icons.Filled.ArrowDropDown, null)
            }

            DropdownMenu(
                expanded = isDropDownExpanded.value,
                onDismissRequest = {
                    isDropDownExpanded.value = false
                },
            ) {
                values.forEach { server ->
                    DropdownMenuItem(
                        text = {
                            Text(text = server.name)
                        },
                        onClick = {
                            isDropDownExpanded.value = false
                            onSelect(server)
                        },
                    )
                }
            }
        }
    }
}
