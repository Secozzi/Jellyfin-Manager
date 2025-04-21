package xyz.secozzi.jellyfinmanager.util

expect object DatabasePassword {
    fun getDatabasePassword(): ByteArray
}
