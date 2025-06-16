package xyz.secozzi.jellyfinmanager.domain.anidb

import xyz.secozzi.jellyfinmanager.domain.anidb.models.AniDBEpisode

interface AniDBRepository {
    suspend fun getEpisodes(aniDBId: Long): List<AniDBEpisode>
}
