package xyz.secozzi.jellyfinmanager.domain.anidb.models

data class AniDBEpisode(
    val englishTitle: String?,
    val romajiTitle: String?,
    val nativeTitle: String?,
    val episodeNumber: Int,
    val duration: Int?,
    val airingDate: String?,
    val rating: String?,
    val summary: String?,
    val aniDBEpisodeType: AniDBEpisodeType,
)
