package xyz.secozzi.jellyfinmanager.di

import org.jellyfin.sdk.Jellyfin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.data.jellyfin.JellyfinRepositoryImpl
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository

val JellyfinModule = module {
    includes(
        JellyfinInstanceModule,
    )
    single { get<Jellyfin>().createApi() }
    singleOf(::JellyfinRepositoryImpl).bind(JellyfinRepository::class)
}

expect val JellyfinInstanceModule: Module
