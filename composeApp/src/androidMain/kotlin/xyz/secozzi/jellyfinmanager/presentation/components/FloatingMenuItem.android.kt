package xyz.secozzi.jellyfinmanager.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun FABMenu(
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onClickButton: (Int) -> Unit,
    modifier: Modifier,
    horizontalAlignment: Alignment.Horizontal,
    buttons: List<Pair<ImageVector, String>>,
) {
    BackHandler(expanded) { onExpanded(false) }

    FloatingActionButtonMenu(
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                modifier =
                Modifier.semantics {
                    traversalIndex = -1f
                    stateDescription = if (expanded) "Expanded" else "Collapsed"
                    contentDescription = "Toggle menu"
                }
                    .animateFloatingActionButton(
                        visible = true,
                        alignment = Alignment.BottomEnd,
                    ),
                checked = expanded,
                onCheckedChange = { onExpanded(!expanded) },
            ) {
                val imageVector by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                    }
                }
                Icon(
                    painter = rememberVectorPainter(imageVector),
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress }),
                )
            }
        },
    ) {
        buttons.forEachIndexed { i, item ->
            FloatingActionButtonMenuItem(
                modifier =
                Modifier.semantics {
                    isTraversalGroup = true
                    // Add a custom a11y action to allow closing the menu when focusing
                    // the last menu item, since the close button comes before the first
                    // menu item in the traversal order.
                    if (i == buttons.size - 1) {
                        customActions =
                            listOf(
                                CustomAccessibilityAction(
                                    label = "Close menu",
                                    action = {
                                        onExpanded(false)
                                        true
                                    },
                                ),
                            )
                    }
                },
                onClick = {
                    onClickButton(i)
                    onExpanded(false)
                },
                icon = { Icon(item.first, contentDescription = null) },
                text = { Text(text = item.second) },
            )
        }
    }
}
