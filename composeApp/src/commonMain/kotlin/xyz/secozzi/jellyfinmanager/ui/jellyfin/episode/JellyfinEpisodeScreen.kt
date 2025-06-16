package xyz.secozzi.jellyfinmanager.ui.jellyfin.episode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dokar.sonner.Toaster
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.serializer.UUIDSerializer
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.episode.JellyfinEpisodeScreenContent
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreen
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreen
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.presentation.utils.bottomBarPadding
import xyz.secozzi.jellyfinmanager.presentation.utils.plus
import xyz.secozzi.jellyfinmanager.presentation.utils.serializableType
import xyz.secozzi.jellyfinmanager.ui.jellyfin.episode.JellyfinEpisodeScreenViewModel.UploadState
import xyz.secozzi.jellyfinmanager.ui.providers.LocalToaster
import xyz.secozzi.jellyfinmanager.ui.theme.spacing
import kotlin.reflect.typeOf

@Serializable
data class JellyfinEpisodeRouteData(
    @Serializable(with = UUIDSerializer::class)
    val itemId: UUID,
    val isSeason: Boolean,
)

@Serializable
data class JellyfinEpisodeRoute(
    val data: JellyfinEpisodeRouteData,
) {
    companion object {
        val typeMap = mapOf(
            typeOf<JellyfinEpisodeRouteData>() to serializableType<JellyfinEpisodeRouteData>(),
        )
    }
}

@Composable
fun JellyfinEpisodeScreen(isSeason: Boolean) {
    val navigator = LocalNavController.current
    val toaster = LocalToaster.current
    val viewModel = koinViewModel<JellyfinEpisodeScreenViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val aniDBId by viewModel.aniDBId.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val availableTypes by viewModel.availableTypes.collectAsStateWithLifecycle()
    val episodeInfo by viewModel.episodeInfo.collectAsStateWithLifecycle()
    val remoteFileList by viewModel.remoteFileList.collectAsStateWithLifecycle()
    val toasterEvent by viewModel.toasterEvent.collectAsStateWithLifecycle(null)

    LaunchedEffect(toasterEvent) {
        toasterEvent?.let { toaster.show(it) }
    }

    Toaster(state = toaster)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Episodes")
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
            )
        },
        bottomBar = {
            if (state.isSuccess()) {
                Button(
                    onClick = viewModel::uploadFiles,
                    enabled = episodeInfo.isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottomBarPadding()),
                ) {
                    when (uploadState) {
                        UploadState.Idle -> {
                            if (isSeason) {
                                Text("Upload episode.nfo files")
                            } else {
                                Text("Upload movie.nfo file")
                            }
                        }
                        is UploadState.Loading -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.then(Modifier.size(24.dp)),
                                    strokeWidth = 2.dp,
                                )

                                Text("${(uploadState as UploadState.Loading).progress}%")
                            }
                        }
                        is UploadState.Error -> {
                            Icon(Icons.Default.Error, null)
                        }
                        is UploadState.Success -> {
                            Icon(Icons.Default.Check, null)
                        }
                    }
                }
            }
        },
    ) { contentPadding ->
        if (state.isWaiting()) {
            LoadingScreen(contentPadding)
            return@Scaffold
        }

        if (state.isError()) {
            ErrorScreen(
                error = state.getError(),
                paddingValues = contentPadding,
            )
            return@Scaffold
        }

        JellyfinEpisodeScreenContent(
            aniDBId = aniDBId,
            selectedType = selectedType,
            availableTypes = availableTypes.toPersistentList(),
            episodeInfo = episodeInfo,
            remoteFileList = remoteFileList.toPersistentList(),
            updateStart = viewModel::updateStart,
            updateEnd = viewModel::updateEnd,
            updateOffset = viewModel::updateOffset,
            onSelectType = viewModel::onSelectType,
            paddingValues = contentPadding,
        )
    }
}
