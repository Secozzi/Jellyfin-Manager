package xyz.secozzi.jellyfinmanager.ui.jellyfin.cover

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.serializer.UUIDSerializer
import org.koin.compose.viewmodel.koinViewModel
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.cover.JellyfinCoverScreenContent
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.presentation.utils.bottomBarPadding
import xyz.secozzi.jellyfinmanager.presentation.utils.serializableType
import kotlin.reflect.typeOf

@Serializable
data class JellyfinCoverRouteData(
    @Serializable(with = UUIDSerializer::class)
    val itemId: UUID,
)

@Serializable
data class JellyfinCoverRoute(
    val data: JellyfinCoverRouteData,
) {
    companion object {
        val typeMap = mapOf(
            typeOf<JellyfinCoverRouteData>() to serializableType<JellyfinCoverRouteData>(),
        )
    }
}

@Composable
fun JellyfinCoverScreen() {
    val navigator = LocalNavController.current
    val viewModel = koinViewModel<JellyfinCoverScreenViewModel>()

    val toastState = rememberToasterState()

    val state by viewModel.state.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val current by viewModel.current.collectAsStateWithLifecycle()
    val toasterEvent by viewModel.toasterEvent.collectAsStateWithLifecycle(null)

    var selectedImage by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(toasterEvent) {
        toasterEvent?.let { toastState.show(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Manage images")
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                },
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    selectedImage?.let {
                        if (it == 0) {
                            viewModel.removeImage()
                        } else {
                            viewModel.uploadImage(state.getData()[it - 1].url)
                        }
                    }
                },
                enabled = selectedImage != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottomBarPadding()),
            ) {
                if (selectedImage == 0) {
                    Text("Remove image")
                } else {
                    Text("Set image")
                }
            }
        },
    ) { contentPadding ->
        Toaster(
            state = toastState,
            containerPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
        )

        JellyfinCoverScreenContent(
            state = state,
            selectedType = selectedType,
            current = current,
            selectedImage = selectedImage,
            onSelectImage = { selection ->
                selectedImage = selection.takeUnless { selectedImage == it }
            },
            paddingValues = contentPadding,
            onSelect = {
                selectedImage = null
                viewModel.onSelectType(it)
            },
        )
    }
}
