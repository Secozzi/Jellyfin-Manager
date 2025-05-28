package xyz.secozzi.jellyfinmanager.presentation.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import xyz.secozzi.jellyfinmanager.ui.theme.spacing
import xyz.secozzi.jellyfinmanager.utils.Platform
import xyz.secozzi.jellyfinmanager.utils.platform

@Composable
@ReadOnlyComposable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(layoutDirection) +
            other.calculateStartPadding(layoutDirection),
        end = calculateEndPadding(layoutDirection) +
            other.calculateEndPadding(layoutDirection),
        top = calculateTopPadding() + other.calculateTopPadding(),
        bottom = calculateBottomPadding() + other.calculateBottomPadding(),
    )
}

@Composable
fun bottomBarPadding(): PaddingValues {
    return WindowInsets.navigationBars.asPaddingValues() + PaddingValues(
        start = MaterialTheme.spacing.medium,
        end = MaterialTheme.spacing.medium,
        bottom = if (platform == Platform.Desktop) MaterialTheme.spacing.smaller else 0.dp,
    )
}
