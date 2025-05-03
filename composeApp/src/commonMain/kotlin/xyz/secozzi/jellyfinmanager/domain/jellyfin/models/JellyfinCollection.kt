package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind

// From https://github.com/jarnedemeulemeester/findroid
enum class CollectionType(val type: String) {
    Movies("movies"),
    TvShows("tvshows"),
    BoxSets("boxsets"),
    Mixed("null"),
    ;

    companion object {
        fun fromString(string: String?): CollectionType? {
            if (string == null) {
                return Mixed
            }

            return try {
                entries.first { it.type == string }
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }
}

data class JellyfinCollection(
    override val name: String,
    override val id: UUID,
    override val parentId: UUID? = null,
    override val image: JellyfinImage,
    override val type: BaseItemKind,
) : JellyfinItem, java.io.Serializable

fun BaseItemDto.toJellyfinCollection(baseUrl: String): JellyfinCollection? {
    val type = CollectionType.fromString(this.collectionType?.serialName)
    return type?.let {
        JellyfinCollection(
            name = this.name ?: "",
            id = this.id,
            image = this.toJellyfinImage(baseUrl),
            type = this.type,
        )
    }
}
