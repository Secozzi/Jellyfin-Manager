package xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.serializer.UUIDSerializer
import xyz.secozzi.jellyfinmanager.presentation.utils.LocalNavController
import xyz.secozzi.jellyfinmanager.presentation.utils.serializableType
import kotlin.reflect.typeOf

@Serializable
data class JellyfinEntryRouteData(
    @Serializable(with = UUIDSerializer::class)
    val itemId: UUID,
)

@Serializable
data class JellyfinEntryRoute(
    val data: JellyfinEntryRouteData,
) {
    companion object {
        val typeMap = mapOf(
            typeOf<JellyfinEntryRouteData>() to serializableType<JellyfinEntryRouteData>(),
        )
    }
}

@Composable
fun JellyfinEntryScreen(itemId: UUID) {
    val navigator = LocalNavController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = itemId.toString())
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        Text("UwU")
    }
}
