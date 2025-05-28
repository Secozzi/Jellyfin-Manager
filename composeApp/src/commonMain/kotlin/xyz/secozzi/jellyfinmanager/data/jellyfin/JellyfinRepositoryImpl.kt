package xyz.secozzi.jellyfinmanager.data.jellyfin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.extensions.authenticateUserByName
import org.jellyfin.sdk.api.client.extensions.imageApi
import org.jellyfin.sdk.api.client.extensions.itemLookupApi
import org.jellyfin.sdk.api.client.extensions.itemUpdateApi
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.remoteImageApi
import org.jellyfin.sdk.api.client.extensions.userApi
import org.jellyfin.sdk.api.client.extensions.userLibraryApi
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.ItemFields
import org.jellyfin.sdk.model.api.ItemSortBy
import org.jellyfin.sdk.model.api.SeriesInfo
import org.jellyfin.sdk.model.api.SeriesInfoRemoteSearchQuery
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinCollection
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinImageInfo
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinImageType
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSearchResult
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinUser
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.toJellyfinCollection
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.toJellyfinImageType
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.toJellyfinItem

class JellyfinRepositoryImpl(
    private val apiClient: ApiClient,
) : JellyfinRepository {
    lateinit var jellyfinUser: JellyfinUser

    override suspend fun loadServer(server: Server) {
        withContext(Dispatchers.IO) {
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

            jellyfinUser = authResult.user?.let {
                JellyfinUser(
                    name = it.name ?: "User",
                    id = it.id,
                )
            } ?: throw UnsupportedOperationException("Unable to retrieve user")
        }
    }

    override suspend fun getLibraries(): List<JellyfinCollection> {
        val baseUrl = apiClient.baseUrl!!

        return withContext(Dispatchers.IO) {
            apiClient.itemsApi.getItems(
                userId = jellyfinUser.id,
                sortBy = listOf(ItemSortBy.NAME),
            ).content.items.mapNotNull {
                it.toJellyfinCollection(baseUrl)
            }
        }
    }

    override suspend fun getItems(parentId: UUID?): List<JellyfinItem> {
        val baseUrl = apiClient.baseUrl!!

        return withContext(Dispatchers.IO) {
            apiClient.itemsApi.getItems(
                userId = jellyfinUser.id,
                parentId = parentId,
                sortBy = listOf(ItemSortBy.NAME),
                fields = listOf(ItemFields.PATH),
            ).content.items.mapNotNull {
                it.toJellyfinItem(baseUrl)
            }
        }
    }

    override suspend fun getItem(id: UUID): BaseItemDto {
        return withContext(Dispatchers.IO) {
            apiClient.userLibraryApi.getItem(
                itemId = id,
                userId = jellyfinUser.id,
            ).content
        }
    }

    override suspend fun updateItem(id: UUID, type: BaseItemKind, item: BaseItemDto) {
        withContext(Dispatchers.IO) {
            apiClient.itemUpdateApi.updateItem(
                itemId = id,
                data = item,
            )
        }
    }

    override suspend fun searchSeries(
        id: UUID,
        searchProvider: String,
        searchQuery: String,
    ): List<JellyfinSearchResult> {
        return withContext(Dispatchers.IO) {
            apiClient.itemLookupApi.getSeriesRemoteSearchResults(
                data = SeriesInfoRemoteSearchQuery(
                    searchInfo = SeriesInfo(
                        name = searchQuery,
                        isAutomated = false,
                    ),
                    itemId = id,
                    searchProviderName = searchProvider,
                    includeDisabledProviders = true,
                ),
            ).content.map { searchItem ->
                JellyfinSearchResult(
                    name = searchItem.name ?: "",
                    year = searchItem.productionYear,
                    imageUrl = searchItem.imageUrl,
                    id = searchItem.providerIds?.get(searchProvider),
                )
            }
        }
    }

    override suspend fun getImages(
        id: UUID,
    ): Map<JellyfinImageType, String> {
        return withContext(Dispatchers.IO) {
            apiClient.imageApi.getItemImageInfos(
                itemId = id,
            ).content
                .mapNotNull {
                    val type = it.imageType.toJellyfinImageType() ?: return@mapNotNull null
                    type to it.imageTag
                }
                .associate { (type, tag) ->
                    type to apiClient.imageApi.getItemImageUrl(
                        itemId = id,
                        imageType = type.imageType,
                        tag = tag,
                    )
                }
        }
    }

    override suspend fun getRemoteImages(id: UUID, imageType: JellyfinImageType): List<JellyfinImageInfo> {
        return withContext(Dispatchers.IO) {
            apiClient.remoteImageApi.getRemoteImages(
                itemId = id,
                type = imageType.imageType,
                includeAllLanguages = true,
            ).content.images?.map { image ->
                JellyfinImageInfo(
                    url = image.url.orEmpty(),
                    provider = image.providerName.orEmpty(),
                    extraInfo = image.width?.let { "$it x ${image.height ?: 0}" },
                )
            }.orEmpty()
        }
    }

    override suspend fun deleteImage(id: UUID, imageType: JellyfinImageType): Boolean {
        return withContext(Dispatchers.IO) {
            apiClient.imageApi.deleteItemImage(id, imageType.imageType).status in 200..299
        }
    }

    override suspend fun uploadImage(id: UUID, imageType: JellyfinImageType, imageUrl: String): Boolean {
        if (imageType == JellyfinImageType.Backdrop) {
            deleteImage(id, imageType)
        }

        return withContext(Dispatchers.IO) {
            apiClient.remoteImageApi.downloadRemoteImage(
                itemId = id,
                type = imageType.imageType,
                imageUrl = imageUrl,
            ).status in 200..299
        }
    }
}
