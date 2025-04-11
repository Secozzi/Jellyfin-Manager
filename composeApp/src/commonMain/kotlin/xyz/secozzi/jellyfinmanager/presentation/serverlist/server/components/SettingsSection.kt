package xyz.secozzi.jellyfinmanager.presentation.serverlist.server.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun SettingsSection(
    title: String,
    scrollable: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = MaterialTheme.spacing.medium),
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
                modifier = if (scrollable) {
                    Modifier.verticalScroll(scrollState)
                } else {
                    Modifier
                }
                    .padding(MaterialTheme.spacing.large),
            ) {
                content()
            }
        }
    }
}
