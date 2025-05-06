package xyz.secozzi.jellyfinmanager.di

import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.domain.database.DatabaseModule

fun initKoin(
    datastorePath: String,
) = module {
    includes(
        PreferencesModule(datastorePath),
        DatabaseModule,
        SSHModule,
        JellyfinModule,
        ViewModelsModule,
    )
}
