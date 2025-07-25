package xyz.secozzi.jellyfinmanager.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
expect fun FABMenu(
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onClickButton: (Int) -> Unit,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.End,
    buttons: List<Pair<ImageVector, String>>,
)
