package xyz.secozzi.jellyfinmanager.di

import org.kodein.di.DI.Module
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import xyz.secozzi.jellyfinmanager.preferences.createDataStore
import xyz.secozzi.jellyfinmanager.preferences.preference.DataStorePreferenceStore
import xyz.secozzi.jellyfinmanager.preferences.preference.PreferenceStore

val PreferencesModule: (String) -> Module = {
    Module("PreferencesModule") {
        bindSingleton { createDataStore { it } }
        bindSingleton<PreferenceStore> { DataStorePreferenceStore(instance()) }
        bindSingleton { BasePreferences(instance()) }
    }
}
