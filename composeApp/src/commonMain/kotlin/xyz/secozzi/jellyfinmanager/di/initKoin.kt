package xyz.secozzi.jellyfinmanager.di

import org.koin.dsl.module

fun initKoin(
    datastorePath: String,
) = module {
    includes(
        PreferencesModule(datastorePath),
    )
}
