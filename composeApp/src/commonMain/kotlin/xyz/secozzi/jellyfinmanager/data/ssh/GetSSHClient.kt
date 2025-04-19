package xyz.secozzi.jellyfinmanager.data.ssh

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.Config
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import xyz.secozzi.jellyfinmanager.domain.database.models.Server

expect val config: Config

class GetSSHClient {
    suspend operator fun invoke(
        server: Server,
    ): SSHClient {
        return withContext(Dispatchers.IO) {
            SSHClient(config).apply {
                addHostKeyVerifier(PromiscuousVerifier())
                connect(server.sshAddress, server.sshPort.toInt())

                if (server.sshPassword.isNotBlank()) {
                    authPassword(server.sshHostname, server.sshPassword)
                } else if (server.sshPrivateKey.isNotBlank()) {
                    val kp = loadKeys(server.sshPrivateKey, null, null)
                    authPublickey(server.sshHostname, kp)
                }
            }
        }
    }
}
