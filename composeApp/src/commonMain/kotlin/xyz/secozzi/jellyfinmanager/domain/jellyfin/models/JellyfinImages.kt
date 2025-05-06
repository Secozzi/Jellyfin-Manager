package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

import com.eygraber.uri.Uri
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.ImageType

// From https://github.com/jarnedemeulemeester/findroid
@Serializable
data class JellyfinImage(
    val primary: Uri? = null,
    // val showPrimary: Uri? = null,
)

fun BaseItemDto.toJellyfinImage(baseUrl: String): JellyfinImage {
    val baseUri = Uri.parse(baseUrl)
    val primary = imageTags?.get(ImageType.PRIMARY)?.let { tag ->
        baseUri.buildUpon().apply {
            appendEncodedPath("items/$id/Images/${ImageType.PRIMARY}")
            appendQueryParameter("tag", tag)
        }.build()
    }

    val showPrimary = seriesPrimaryImageTag?.let { tag ->
        baseUri.buildUpon().apply {
            appendEncodedPath("items/$id/Images/${ImageType.PRIMARY}")
            appendQueryParameter("tag", tag)
        }.build()
    }

    return JellyfinImage(
        primary = primary ?: showPrimary,
    )
}
