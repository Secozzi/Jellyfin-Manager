package xyz.secozzi.jellyfinmanager.data.ssh

import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.utils.writeToFile
import java.io.IOException

class SftpWriteFile(
    private val getSSHClient: GetSSHClient,
) {
    suspend operator fun invoke(
        server: Server,
        fileContents: String,
        filePath: String,
    ) {
        val client = getSSHClient(server)

        try {
            val sftpClient = client.newSFTPClient()
                ?: throw IOException("No active SSH client")

            sftpClient.writeToFile(filePath, fileContents)
        } finally {
            client.disconnect();
        }
    }
}
