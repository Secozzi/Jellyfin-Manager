package xyz.secozzi.jellyfinmanager.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastType

@Composable
fun ErrorScreenContent(
    throwable: Throwable,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit) = {},
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Outlined.BugReport,
                null,
                modifier = Modifier
                    .size(96.dp)
            )
        }
        Text(
            text = "An error has occurred",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            throwable.message!!,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        content()
        // LogsContainer(throwable.stackTraceToString())
    }
}

fun errorToast(message: String) = Toast(message, type = ToastType.Error)
