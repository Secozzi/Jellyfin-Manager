package xyz.secozzi.jellyfinmanager.domain.anilist

import xyz.secozzi.jellyfinmanager.domain.anilist.models.AnilistDetails

interface AnilistRepository {
    suspend fun getDetails(id: Long): AnilistDetails?
}
