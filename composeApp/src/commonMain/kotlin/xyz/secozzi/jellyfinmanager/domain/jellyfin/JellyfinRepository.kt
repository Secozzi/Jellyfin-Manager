package xyz.secozzi.jellyfinmanager.domain.jellyfin

import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinCollection
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSearchResult

interface JellyfinRepository {
    suspend fun loadServer(server: Server)

    suspend fun getLibraries(): List<JellyfinCollection>

    suspend fun getItems(parentId: UUID?): List<JellyfinItem>

    suspend fun getItem(id: UUID): BaseItemDto

    suspend fun updateItem(id: UUID, type: BaseItemKind, item: BaseItemDto)

    suspend fun searchSeries(id: UUID, searchProvider: String, searchQuery: String): List<JellyfinSearchResult>
}
