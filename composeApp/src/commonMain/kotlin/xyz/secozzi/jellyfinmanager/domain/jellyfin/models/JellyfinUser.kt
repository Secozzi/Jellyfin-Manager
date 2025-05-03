package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

import org.jellyfin.sdk.model.UUID

data class JellyfinUser(
    val name: String,
    val id: UUID,
)
