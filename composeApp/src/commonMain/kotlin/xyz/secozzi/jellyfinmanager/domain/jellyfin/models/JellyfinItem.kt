package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind

interface JellyfinItem {
    val id: UUID
    val parentId: UUID?
    val name: String
    val image: JellyfinImage
    val type: BaseItemKind

    val path: String
    val overview: String
    val genres: List<String>
    val studios: List<String>
}

fun BaseItemDto.toJellyfinItem(baseUrl: String): JellyfinItem? {
    return when (type) {
        BaseItemKind.MOVIE -> toJellyfinMovie(baseUrl)
        BaseItemKind.SEASON -> toJellyfinSeason(baseUrl)
        BaseItemKind.SERIES -> toJellyfinSeries(baseUrl)
        BaseItemKind.EPISODE -> toJellyfinEpisode(baseUrl)
        else -> null
    }
}
