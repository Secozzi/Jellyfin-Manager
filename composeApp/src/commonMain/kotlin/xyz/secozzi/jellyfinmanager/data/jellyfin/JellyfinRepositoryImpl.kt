package xyz.secozzi.jellyfinmanager.data.jellyfin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.authenticateUserByName
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.ItemFields
import org.jellyfin.sdk.model.api.ItemSortBy
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinCollection
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinUser
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.toJellyfinCollection
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.toJellyfinItem

class JellyfinRepositoryImpl(
    private val apiClient: ApiClient,
) : JellyfinRepository {
    override suspend fun loadServer(server: Server): JellyfinUser {
        apiClient.update(
            baseUrl = server.jfAddress,
        )

        val authResult by apiClient.userApi.authenticateUserByName(
            username = server.jfUsername,
            password = server.jfPassword,
        )

        apiClient.update(
            accessToken = authResult.accessToken,
        )

        return authResult.user?.let {
            JellyfinUser(
                name = it.name ?: "User",
                id = it.id,
            )
        } ?: throw UnsupportedOperationException("Unable to retrieve user")
    }

    override suspend fun getLibraries(user: JellyfinUser): List<JellyfinCollection> {
        val baseUrl = apiClient.baseUrl!!

        return withContext(Dispatchers.IO) {
            apiClient.itemsApi.getItems(
                userId = user.id,
                sortBy = listOf(ItemSortBy.NAME),
            ).content.items.mapNotNull {
                it.toJellyfinCollection(baseUrl)
            }
        }
    }

    override suspend fun getItems(user: JellyfinUser, parentId: UUID?): List<JellyfinItem> {
        val baseUrl = apiClient.baseUrl!!

        return withContext(Dispatchers.IO) {
            apiClient.itemsApi.getItems(
                userId = user.id,
                parentId = parentId,
                sortBy = listOf(ItemSortBy.NAME),
                fields = listOf(ItemFields.PATH),
            ).content.items.mapNotNull {
                it.toJellyfinItem(baseUrl)
            }
        }
    }
}
