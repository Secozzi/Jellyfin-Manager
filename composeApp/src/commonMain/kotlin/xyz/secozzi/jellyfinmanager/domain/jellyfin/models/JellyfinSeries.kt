package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind

data class JellyfinSeries(
    override val name: String,
    override val id: UUID,
    override val parentId: UUID?,
    override val image: JellyfinImage,
    override val type: BaseItemKind,
) : JellyfinItem

fun BaseItemDto.toJellyfinSeries(baseUrl: String): JellyfinSeries {
    return JellyfinSeries(
        name = this.name ?: "",
        id = this.id,
        parentId = this.parentId,
        image = this.toJellyfinImage(baseUrl),
        type = this.type,
    )
}
