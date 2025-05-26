package xyz.secozzi.jellyfinmanager.domain.anilist.models

import org.jellyfin.sdk.model.DateTime

data class AnilistDetails(
    val titles: List<String>,
    val description: String?,
    val genre: List<String>,
    val studio: List<String>,
    val startDate: DateTime?,
    val endDate: DateTime?,
    val status: AnilistStatus,
)
