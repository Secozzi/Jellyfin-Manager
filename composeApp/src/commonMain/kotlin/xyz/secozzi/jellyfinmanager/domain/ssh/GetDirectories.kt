package xyz.secozzi.jellyfinmanager.domain.ssh

import net.schmizz.sshj.SSHClient
import xyz.secozzi.jellyfinmanager.data.ssh.ExecuteSSH
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.ssh.model.Directory

class GetDirectories(
    private val executeSSH: ExecuteSSH,
) {
    suspend operator fun invoke(
        sshClient: SSHClient?,
        server: Server,
        path: String,
    ): List<Directory> {
        val commandResult = executeSSH(
            server = server,
            sshClient = sshClient,
            commands = listOf(
                "/usr/bin/ls",
                path,
                "-l1h",
                "--full-time",
                "--group-directories-first",
            ),
        )

        val directories = lsRegex.findAll(commandResult).map { m ->
            val (type, count, size, date, time, name) = m.destructured
            val isDirectory = type == "d"

            val extraData = if (isDirectory) {
                val dirCount = count.toInt() - 2
                val suffix = if (dirCount == 1) "item" else "items"

                "$dirCount $suffix"
            } else {
                val suffix = if (size.last().isDigit()) "B" else "iB"

                "$size $suffix"
            }

            val lastModified = "$date ${time.substringBefore(".")}"

            Directory(
                name = name.removePrefix("'").removeSuffix("'"),
                isDirectory = isDirectory,
                date = lastModified,
                extraData = extraData,
            )
        }.toList()

        return if (server.sshBaseDirBlacklist.isBlank()) {
            directories
        } else {
            val blacklist = server.sshBaseDirBlacklist.split(',')
            directories.filterNot { it.name in blacklist }
        }
    }

    private val lsRegex =
        Regex(
            """([d\-])[\w-]{9}\s+(\d+)\s+\S+\s+\S+\s+(\S+)\s+(\S+)\s+(\S+)\s+\S+\s+(.+)$""",
            RegexOption.MULTILINE,
        )
}
