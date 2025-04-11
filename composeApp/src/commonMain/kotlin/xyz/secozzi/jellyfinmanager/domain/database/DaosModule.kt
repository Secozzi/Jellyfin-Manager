package xyz.secozzi.jellyfinmanager.domain.database

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.domain.daos.ServerDao

val DaosModule = module {
    singleOf(::ServerDao)
}
