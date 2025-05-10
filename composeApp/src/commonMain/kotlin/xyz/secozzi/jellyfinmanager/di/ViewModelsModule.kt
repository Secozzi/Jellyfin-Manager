package xyz.secozzi.jellyfinmanager.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.ssh.SSHScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.ServerListScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server.ServerScreenViewModel

val ViewModelsModule = module {
    singleOf(::HomeScreenViewModel)
    factoryOf(::ServerListScreenViewModel)
    factoryOf(::SSHScreenViewModel)
    factoryOf(::JellyfinScreenViewModel)
    factoryOf(::ServerScreenViewModel)
    factoryOf(::JellyfinEntryScreenViewModel)
}
