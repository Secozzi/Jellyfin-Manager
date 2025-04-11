package xyz.secozzi.jellyfinmanager.domain.usecase

import kotlinx.coroutines.flow.Flow
import xyz.secozzi.jellyfinmanager.domain.daos.ServerDao
import xyz.secozzi.jellyfinmanager.domain.database.models.Server

class ServerUseCase(
    private val dao: ServerDao,
) {
    suspend fun upsert(server: Server) {
        if (server.id == Server.UNINITIALIZED_ID) {
            val index = if (server.index == Server.UNINITIALIZED_INDEX) {
                dao.getServerCount()
            } else {
                server.index
            }

            dao.insert(
                server.copy(
                    index = index,
                )
            )
        } else {
            dao.update(server)
        }
    }

    fun getServers(): Flow<List<Server>> {
        return dao.getServers()
    }

    suspend fun increaseIndex(server: Server) {
        val nextServer = dao.getNextServer(server.index) ?: return

        val current = server.copy(index = nextServer.index)
        val next = nextServer.copy(index = server.index)

        upsert(current)
        upsert(next)
    }

    suspend fun decreaseIndex(server: Server) {
        val previousServer = dao.getPreviousServer(server.index) ?: return

        val current = server.copy(index = previousServer.index)
        val previous = previousServer.copy(index = server.index)

        upsert(current)
        upsert(previous)
    }

    suspend fun delete(server: Server) {
        val serversAfter = dao.getServersAfter(server.index)

        dao.delete(server.id)

        serversAfter.forEach { s ->
            val updatedServer = s.copy(index = s.index - 1)
            upsert(updatedServer)
        }
    }
}
