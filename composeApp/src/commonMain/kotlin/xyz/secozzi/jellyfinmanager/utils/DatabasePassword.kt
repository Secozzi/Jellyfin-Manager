package xyz.secozzi.jellyfinmanager.utils

expect object DatabasePassword {
    fun getDatabasePassword(): ByteArray
}
