package xyz.secozzi.jellyfinmanager.domain.database.models

import kotlinx.serialization.Serializable

@Serializable
data class Server(
    val id: Long = UNINITIALIZED_ID,
    val name: String,
    val index: Long = UNINITIALIZED_INDEX,

    val sshAddress: String,
    val sshPort: Long,
    val sshHostname: String,
    val sshPassword: String,
    val sshPrivateKey: String,
    val sshBaseDir: String,
    val sshBaseDirBlacklist: String,

    val jfAddress: String,
    val jfUsername: String,
    val jfPassword: String,
) : java.io.Serializable {
    companion object {
        const val UNINITIALIZED_ID = 0L
        const val UNINITIALIZED_INDEX = -1L
    }
}
