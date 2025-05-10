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
    override val path: String,
    override val overview: String,
    override val genres: List<String>,
    override val studios: List<String>,
    val episodeNumber: Int,
) : JellyfinItem

fun BaseItemDto.toJellyfinEpisode(baseUrl: String): JellyfinEpisode {
    return JellyfinEpisode(
        name = this.name ?: "",
        id = this.id,
        parentId = this.parentId,
        image = this.toJellyfinImage(baseUrl),
        type = this.type,
        path = this.path ?: "",
        overview = this.overview ?: "",
        genres = this.genres.orEmpty(),
        studios = this.studios?.mapNotNull { it.name }.orEmpty(),
        episodeNumber = this.indexNumber ?: 0,
    )
}
