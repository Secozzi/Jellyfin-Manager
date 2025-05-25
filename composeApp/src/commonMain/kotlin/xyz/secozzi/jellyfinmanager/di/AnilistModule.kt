package xyz.secozzi.jellyfinmanager.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.data.anilist.AnilistRepositoryImpl
import xyz.secozzi.jellyfinmanager.domain.anilist.AnilistRepository

val AnilistModule = module {
    singleOf(::AnilistRepositoryImpl).bind(AnilistRepository::class)
}
