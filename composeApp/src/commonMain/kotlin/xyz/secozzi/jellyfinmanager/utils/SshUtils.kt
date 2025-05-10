package xyz.secozzi.jellyfinmanager.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.OpenMode
import net.schmizz.sshj.sftp.SFTPClient
import java.io.IOException

fun SFTPClient.writeToFile(path: String, content: String) {
    val data = content.toByteArray()
    open(path, setOf(OpenMode.CREAT, OpenMode.WRITE)).use { remoteFile ->
        remoteFile.write(0, data, 0, data.size)
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
