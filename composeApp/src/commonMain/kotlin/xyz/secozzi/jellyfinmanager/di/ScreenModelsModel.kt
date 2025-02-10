package xyz.secozzi.jellyfinmanager.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.ui.home.tabs.SSHTabScreenModel

val ScreenModelsModule = module {
    factoryOf(::SSHTabScreenModel)
}
