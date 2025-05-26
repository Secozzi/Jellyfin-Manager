package xyz.secozzi.jellyfinmanager.domain.anilist.models

enum class AnilistStatus(val jellyfinName: String) {
    Unknown(""),
    Ongoing("Continuing"),
    Completed("Ended"),
}
