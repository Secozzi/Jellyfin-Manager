package xyz.secozzi.jellyfinmanager.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.data.anidb.AniDBRepositoryImpl
import xyz.secozzi.jellyfinmanager.domain.anidb.AniDBRepository

val AniDBModule = module {
    singleOf(::AniDBRepositoryImpl).bind(AniDBRepository::class)
}
