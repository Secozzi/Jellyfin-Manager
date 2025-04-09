package xyz.secozzi.jellyfinmanager

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform