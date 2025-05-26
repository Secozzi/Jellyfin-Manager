package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

data class JellyfinSearchResult(
    val name: String,
    val year: Int?,
    val imageUrl: String?,
    val id: String?,
)

enum class JellyfinSearchProvider(val providerName: String) {
    Anilist("AniList"),
    AniDB("AniDB"),
}
