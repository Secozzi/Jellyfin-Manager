package xyz.secozzi.jellyfinmanager.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
actual fun FABMenu(
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onClickButton: (Int) -> Unit,
    modifier: Modifier,
    horizontalAlignment: Alignment.Horizontal,
    buttons: List<Pair<ImageVector, String>>,
) {
    throw Exception("Unused")
}
