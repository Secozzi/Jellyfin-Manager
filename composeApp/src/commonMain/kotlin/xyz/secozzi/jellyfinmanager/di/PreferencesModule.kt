package xyz.secozzi.jellyfinmanager.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import xyz.secozzi.jellyfinmanager.preferences.createDataStore
import xyz.secozzi.jellyfinmanager.preferences.preference.DataStorePreferenceStore
import xyz.secozzi.jellyfinmanager.preferences.preference.PreferenceStore

val PreferencesModule: (String) -> Module = { dataStorePath ->
    module {
        single { createDataStore { dataStorePath } }
        singleOf(::DataStorePreferenceStore).bind(PreferenceStore::class)
        singleOf(::BasePreferences)
    }
}
