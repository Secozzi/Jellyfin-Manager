package xyz.secozzi.jellyfinmanager.ui.jellyfin.entry

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.serializer.UUIDSerializer
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry.JellyfinEntryScreenContent
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreenContent
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreenContent
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.presentation.utils.serializableType
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinItemType
import xyz.secozzi.jellyfinmanager.ui.theme.spacing
import kotlin.reflect.typeOf

@Serializable
data class JellyfinEntryRouteData(
    @Serializable(with = UUIDSerializer::class)
    val itemId: UUID,
    val itemType: JellyfinItemType,
)

@Serializable
data class JellyfinEntryRoute(
    val data: JellyfinEntryRouteData,
) {
    companion object {
        val typeMap = mapOf(
            typeOf<JellyfinEntryRouteData>() to serializableType<JellyfinEntryRouteData>(),
            typeOf<JellyfinItemType>() to serializableType<JellyfinItemType>(),
        )
    }
}

@Composable
fun JellyfinEntryScreen(type: JellyfinItemType) {
    val navigator = LocalNavController.current
    val viewModel = koinViewModel<JellyfinEntryScreenViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsState()
    val item by viewModel.details.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Edit ${type.name.lowercase()}")
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = viewModel::save,
                enabled = state.isSuccess(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .padding(horizontal = MaterialTheme.spacing.medium),
            ) {
                when (saveState) {
                    JellyfinEntryScreenViewModel.SaveState.Idle -> {
                        Text("Save to ${type.name.lowercase()}.nfo")
                    }
                    JellyfinEntryScreenViewModel.SaveState.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.then(Modifier.size(24.dp)),
                            strokeWidth = 2.dp,
                        )
                    }
                    JellyfinEntryScreenViewModel.SaveState.Error -> {
                        Icon(Icons.Default.Error, null)
                    }
                    JellyfinEntryScreenViewModel.SaveState.Success -> {
                        Icon(Icons.Default.Check, null)
                    }
                }
            }
        }
    ) { contentPadding ->
        if (state.isLoading() || state.isIdle()) {
            LoadingScreenContent()
            return@Scaffold
        }

        if (state.isError()) {
            ErrorScreenContent(
                error = state.getError(),
                modifier = Modifier.fillMaxSize(),
            )
            return@Scaffold
        }

        JellyfinEntryScreenContent(
            item = item,
            onTitleChange = viewModel::onTitleChange,
            onStudioChange = viewModel::onStudioChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onGenreChange = viewModel::onGenreChange,
            modifier = Modifier.padding(contentPadding),
        )
    }
}
