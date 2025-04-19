package xyz.secozzi.jellyfinmanager.domain.ssh.model

data class Directory(
    val name: String,
    val isDirectory: Boolean,
    val date: String? = null,
    val extraData: String? = null,
)
