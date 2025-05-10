package xyz.secozzi.jellyfinmanager.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.data.server.ServerStateHolderImpl
import xyz.secozzi.jellyfinmanager.domain.server.ServerStateHolder

val StateHolderModule = module {
    singleOf(::ServerStateHolderImpl).bind(ServerStateHolder::class)
}
