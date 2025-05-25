package xyz.secozzi.jellyfinmanager.data.anilist

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.secozzi.jellyfinmanager.domain.anilist.AnilistRepository
import xyz.secozzi.jellyfinmanager.domain.anilist.models.AnilistDetails

class AnilistRepositoryImpl(
    private val client: HttpClient,
) : AnilistRepository {
    override suspend fun getDetails(id: Long): AnilistDetails? {
        val media = withContext(Dispatchers.IO) {
            client.post("https://graphql.anilist.co") {
                contentType(ContentType.Application.Json)
                setBody(
                    AnilistDetailsPostData(
                        query = DETAILS_QUERY,
                        variables = AnilistDetailsVariablesData(id = id),
                    ),
                )
            }.body<AnilistDetailsDto>().data.media
        } ?: return null

        return AnilistDetails(
            titles = listOfNotNull(media.title.english, media.title.romaji, media.title.native),
            description = media.description,
            genre = media.genres,
            studio = media.studios.edges.filter { it.isMain }.map { it.node.name }.take(2),
        )
    }
}

const val DETAILS_QUERY = $$"""query ($id: Int) {
	Media (id: $id) {
		title {
			romaji
			english
			native
		}
		description
		genres
		studios {
			edges {
				isMain
				node {
					name
				}
			}
		}
	}
}
"""
