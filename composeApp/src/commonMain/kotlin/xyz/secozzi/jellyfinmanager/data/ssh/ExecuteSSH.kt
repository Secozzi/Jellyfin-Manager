package xyz.secozzi.jellyfinmanager.data.ssh

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import java.io.IOException

class ExecuteSSH(
    private val getSSHClient: GetSSHClient,
) {
    suspend operator fun invoke(
        server: Server,
        sshClient: SSHClient?,
        commands: List<String>,
    ): String {
        val client = if (sshClient?.isConnected == false) {
            getSSHClient(server)
        } else {
            sshClient
        }

        val escapedCommand = commands.joinToString(" ") { cmd ->
            if (cmd.contains(" ")) "\"$cmd\"" else cmd
        }

        return withContext(Dispatchers.IO) {
            client?.startSession()?.use { session ->
                session.exec(escapedCommand).use { cmd ->
                    String(cmd.inputStream.readBytes())
                }
            } ?: throw IOException("No active SSH client")
        }
    }
}
