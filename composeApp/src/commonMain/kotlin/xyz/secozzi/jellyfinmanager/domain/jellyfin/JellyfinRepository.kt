package xyz.secozzi.jellyfinmanager.domain.jellyfin

import org.jellyfin.sdk.model.UUID
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinCollection
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinUser

interface JellyfinRepository {
    suspend fun loadServer(server: Server)

    suspend fun getLibraries(): List<JellyfinCollection>

    suspend fun getItems(parentId: UUID?): List<JellyfinItem>

    suspend fun getItem(id: UUID): JellyfinItem?
}
