package xyz.secozzi.jellyfinmanager.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.Config
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.OpenMode
import net.schmizz.sshj.sftp.SFTPClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import java.io.IOException

expect val config: Config

suspend fun getSSHClient(
    address: String,
    hostName: String,
    password: String,
    port: Int,
): SSHClient {
    return withContext(Dispatchers.IO) {
        SSHClient(config).apply {
            addHostKeyVerifier(PromiscuousVerifier())
            connect(address, port)
            authPassword(hostName, password)
        }
    }
}

suspend fun executeSSH(
    client: SSHClient?,
    commands: List<String>,
): String {
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

fun SFTPClient.writeToFile(path: String, content: String) {
    open(path, setOf(OpenMode.CREAT, OpenMode.WRITE)).use { remoteFile ->
        remoteFile.write(0, content.toByteArray(), 0, content.length)
    }
}

suspend fun <R> useSFTPClient(
    client: SSHClient?,
    block: suspend (SFTPClient) -> R,
) : R {
    return withContext(Dispatchers.IO) {
        client?.newSFTPClient()?.use { sftp ->
            block(sftp)
        } ?: throw IOException("No active SSH client")
    }
}
