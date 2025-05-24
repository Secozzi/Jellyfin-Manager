package xyz.secozzi.jellyfinmanager.domain.daos

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.domain.db.JMDatabase

class ServerDao(
    db: JMDatabase,
) {
    private val queries = db.jMDatabaseQueries

    suspend fun insert(server: Server) {
        withContext(Dispatchers.IO) {
            queries.insert(
                name = server.name,
                index = server.index,
                sshAddress = server.sshAddress,
                sshPort = server.sshPort,
                sshHostname = server.sshHostname,
                sshPassword = server.sshPassword,
                sshPrivateKey = server.sshPrivateKey,
                sshBaseDir = server.sshBaseDir,
                sshBaseDirBlacklist = server.sshBaseDirBlacklist,
                jfAddress = server.jfAddress,
                jfUsername = server.jfUsername,
                jfPassword = server.jfPassword,
            )
        }
    }

    suspend fun update(server: Server) {
        withContext(Dispatchers.IO) {
            queries.update(
                id = server.id,
                name = server.name,
                index = server.index,
                sshAddress = server.sshAddress,
                sshPort = server.sshPort,
                sshHostname = server.sshHostname,
                sshPassword = server.sshPassword,
                sshPrivateKey = server.sshPrivateKey,
                sshBaseDir = server.sshBaseDir,
                sshBaseDirBlacklist = server.sshBaseDirBlacklist,
                jfAddress = server.jfAddress,
                jfUsername = server.jfUsername,
                jfPassword = server.jfPassword,
            )
        }
    }

    suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            queries.delete(id)
        }
    }

    fun getServers(): Flow<List<Server>> {
        return queries
            .getServers(::mapServer)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    suspend fun getNextServer(index: Long): Server? {
        return withContext(Dispatchers.IO) {
            queries.getNextServer(index, ::mapServer).executeAsOneOrNull()
        }
    }

    suspend fun getPreviousServer(index: Long): Server? {
        return withContext(Dispatchers.IO) {
            queries.getPreviousServer(index, ::mapServer).executeAsOneOrNull()
        }
    }

    suspend fun getServersAfter(index: Long): List<Server> {
        return withContext(Dispatchers.IO) {
            queries.getServersAfter(index, ::mapServer).executeAsList()
        }
    }

    suspend fun getServerCount(): Long {
        return withContext(Dispatchers.IO) {
            queries.getServerCount().executeAsOne()
        }
    }

    private fun mapServer(
        id: Long,
        name: String,
        index: Long,
        sshAddress: String,
        sshPort: Long,
        sshHostname: String,
        sshPassword: String,
        sshPrivateKey: String,
        sshBaseDir: String,
        sshBaseDirBlacklist: String,
        jfAddress: String,
        jfUsername: String,
        jfPassword: String,
    ): Server {
        return Server(
            id = id,
            name = name,
            index = index,
            sshAddress = sshAddress,
            sshPort = sshPort,
            sshHostname = sshHostname,
            sshPassword = sshPassword,
            sshPrivateKey = sshPrivateKey,
            sshBaseDir = sshBaseDir,
            sshBaseDirBlacklist = sshBaseDirBlacklist,
            jfAddress = jfAddress,
            jfUsername = jfUsername,
            jfPassword = jfPassword,
        )
    }
}
