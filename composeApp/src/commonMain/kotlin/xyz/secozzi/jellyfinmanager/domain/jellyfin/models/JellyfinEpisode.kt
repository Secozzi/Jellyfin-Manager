package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind

data class JellyfinEpisode(
    override val name: String,
    override val id: UUID,
    override val parentId: UUID? = null,
    override val image: JellyfinImage,
    override val type: BaseItemKind,
    val episodeNumber: Int,
    val path: String,
) : JellyfinItem, java.io.Serializable

fun BaseItemDto.toJellyfinEpisode(baseUrl: String): JellyfinEpisode {
    return JellyfinEpisode(
        name = this.name ?: "",
        id = this.id,
        parentId = this.parentId,
        image = this.toJellyfinImage(baseUrl),
        type = this.type,
        episodeNumber = this.indexNumber ?: 0,
        path = this.path ?: "",
    )
}
