package xyz.secozzi.jellyfinmanager.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.jellyfin.search.JellyfinSearchScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.ServerListScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.preferences.serverlist.server.ServerScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.ssh.SSHScreenViewModel

val ViewModelsModule = module {
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::ServerListScreenViewModel)
    viewModelOf(::SSHScreenViewModel)
    viewModelOf(::JellyfinScreenViewModel)
    viewModelOf(::ServerScreenViewModel)
    viewModelOf(::JellyfinEntryScreenViewModel)
    viewModelOf(::JellyfinSearchScreenViewModel)
}
