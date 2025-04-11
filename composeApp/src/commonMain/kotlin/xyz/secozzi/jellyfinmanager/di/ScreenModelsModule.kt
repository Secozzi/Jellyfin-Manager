package xyz.secozzi.jellyfinmanager.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreenScreenModel
import xyz.secozzi.jellyfinmanager.ui.home.tabs.jellyfin.JellyfinTabScreenModel
import xyz.secozzi.jellyfinmanager.ui.home.tabs.ssh.SSHTabScreenModel
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.ServerListScreenModel
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server.ServerScreenModel

val ScreenModelsModule = module {
    factoryOf(::HomeScreenScreenModel)
    factoryOf(::ServerListScreenModel)
    factory { (server: Server) -> SSHTabScreenModel(server) }
    factory { (server: Server) -> JellyfinTabScreenModel(server) }
    factory { (server: Server?, serverNames: List<String>) -> ServerScreenModel(server, serverNames, get()) }
}
