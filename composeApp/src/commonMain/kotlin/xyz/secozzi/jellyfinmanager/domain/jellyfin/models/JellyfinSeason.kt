package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind

data class JellyfinSeason(
    override val name: String,
    override val id: UUID,
    override val parentId: UUID?,
    override val image: JellyfinImage,
    override val type: BaseItemKind,
) : JellyfinItem, java.io.Serializable

fun BaseItemDto.toJellyfinSeason(baseUrl: String): JellyfinSeason {
    return JellyfinSeason(
        name = this.name ?: "",
        id = this.id,
        parentId = this.parentId,
        image = this.toJellyfinImage(baseUrl),
        type = this.type,
    )
}
