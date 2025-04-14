package xyz.secozzi.jellyfinmanager.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreenScreenModel
import xyz.secozzi.jellyfinmanager.ui.home.tabs.jellyfin.JellyfinTabScreenModel
import xyz.secozzi.jellyfinmanager.ui.home.tabs.ssh.SSHTabScreenModel
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.ServerListScreenModel
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server.ServerScreenModel

val ScreenModelsModule = module {
    singleOf(::HomeScreenScreenModel)
    factoryOf(::ServerListScreenModel)
    factoryOf(::SSHTabScreenModel)
    factoryOf(::JellyfinTabScreenModel)
    factory { (server: Server?, serverNames: List<String>) -> ServerScreenModel(server, serverNames, get()) }
}
