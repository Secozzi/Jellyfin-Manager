package xyz.secozzi.jellyfinmanager.domain.anilist.models

data class AnilistDetails(
    val titles: List<String>,
    val description: String?,
    val genre: List<String>,
    val studio: List<String>,
)
