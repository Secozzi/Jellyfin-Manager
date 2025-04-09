package xyz.secozzi.jellyfinmanager.di

import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

fun initKoin(
    datastorePath: String,
) = module {
    includes(
        PreferencesModule(datastorePath),
        // DatabaseModule,
        // ScreenModelsModule,
        // JellyfinModule,
    )
}
