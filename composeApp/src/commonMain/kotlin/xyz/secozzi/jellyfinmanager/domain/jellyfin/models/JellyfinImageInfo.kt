package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

data class JellyfinImageInfo(
    val url: String,
    val provider: String,
    val extraInfo: String?,
)
