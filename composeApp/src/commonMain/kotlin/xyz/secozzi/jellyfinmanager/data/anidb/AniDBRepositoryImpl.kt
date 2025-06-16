package xyz.secozzi.jellyfinmanager.data.anidb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import me.sujanpoudel.utils.paths.appCacheDirectory
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Response
import xyz.secozzi.jellyfinmanager.BuildKonfig
import xyz.secozzi.jellyfinmanager.domain.anidb.AniDBRepository
import xyz.secozzi.jellyfinmanager.domain.anidb.models.AniDBEpisode
import xyz.secozzi.jellyfinmanager.utils.get
import java.io.File
import java.util.concurrent.TimeUnit

class AniDBRepositoryImpl(
    client: OkHttpClient,
    private val xml: XML,
) : AniDBRepository {
    val aniDBClient = client.newBuilder()
        .cache(
            Cache(
                directory = File(appCacheDirectory(BuildKonfig.APP_ID).toString(), "anidb_cache"),
                maxSize = 10L * 1024 * 1024, // 10 MiB
            ),
        )
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val cachedRequest = originalRequest.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()

            val response = chain.proceed(cachedRequest)

            if (response.code == 504 || isResponseTooOld(response)) {
                response.close()

                val cache = CacheControl.Builder()
                    .maxAge(1, TimeUnit.DAYS)
                    .build()

                val newRequest = originalRequest.newBuilder()
                    .cacheControl(cache)
                    .build()

                return@addInterceptor chain.proceed(newRequest)
            }

            response
        }
        .build()

    private fun isResponseTooOld(response: Response): Boolean {
        val responseTime = response.receivedResponseAtMillis
        val currentTime = System.currentTimeMillis()
        val age = (currentTime - responseTime) / 1000
        return age >= 60 * 60 * 24 // 1 dau
    }

    override suspend fun getEpisodes(aniDBId: Long): List<AniDBEpisode> {
        val url = "http://api.anidb.net:9001/httpapi".toHttpUrl().newBuilder().apply {
            addQueryParameter("request", "anime")
            addQueryParameter("client", CLIENT_NAME)
            addQueryParameter("clientver", CLIENT_VERSION)
            addQueryParameter("protover", "1")
            addQueryParameter("aid", aniDBId.toString())
        }.build()

        val xml = withContext(Dispatchers.IO) {
            xml.decodeFromString<AniDBAnimeDto>(aniDBClient.get(url).body.string())
        }

        return xml.episodes.episode.map { it.toEpisodeModel() }
    }

    companion object {
        private const val CLIENT_NAME = "axusqopthw"
        private const val CLIENT_VERSION = "1"
    }
}
