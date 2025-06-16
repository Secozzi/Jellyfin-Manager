package xyz.secozzi.jellyfinmanager.data.anilist

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import org.jellyfin.sdk.model.DateTime
import xyz.secozzi.jellyfinmanager.domain.anilist.AnilistRepository
import xyz.secozzi.jellyfinmanager.domain.anilist.models.AnilistDetails
import xyz.secozzi.jellyfinmanager.domain.anilist.models.AnilistStatus
import xyz.secozzi.jellyfinmanager.utils.parseAs
import xyz.secozzi.jellyfinmanager.utils.post
import xyz.secozzi.jellyfinmanager.utils.toRequestBody

class AnilistRepositoryImpl(
    private val client: OkHttpClient,
    private val json: Json,
) : AnilistRepository {
    override suspend fun getDetails(id: Long): AnilistDetails? {
        val media = withContext(Dispatchers.IO) {
            with(json) {
                client.post(
                    url = "https://graphql.anilist.co".toHttpUrl(),
                    body = AnilistDetailsPostData(
                        query = DETAILS_QUERY,
                        variables = AnilistDetailsVariablesData(id = id),
                    ).toRequestBody(),
                ).parseAs<AnilistDetailsDto>().data.media
            }
        } ?: return null

        return AnilistDetails(
            titles = listOfNotNull(media.title.english, media.title.romaji, media.title.native),
            description = media.description,
            genre = media.genres,
            studio = media.studios.edges.filter { it.isMain }.map { it.node.name }.take(2),
            startDate = media.startDate.let {
                DateTime.of(
                    it.year ?: DateTime.MIN.year,
                    it.month ?: 1,
                    it.day ?: 1,
                    0,
                    0,
                )
            },
            endDate = media.endDate.let {
                DateTime.of(
                    it.year ?: DateTime.MIN.year,
                    it.month ?: 1,
                    it.day ?: 1,
                    0,
                    0,
                )
            },
            status = media.status.toStatus(),
        )
    }

    private fun String?.toStatus(): AnilistStatus {
        return when (this?.lowercase()) {
            "finished" -> AnilistStatus.Completed
            "releasing" -> AnilistStatus.Ongoing
            else -> AnilistStatus.Unknown
        }
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
		startDate {
			year
			month
			day
		}
		endDate {
			year
			month
			day
		}
		status
	}
}
"""
