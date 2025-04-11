package xyz.secozzi.jellyfinmanager.domain.database

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.domain.usecase.ServerUseCase

val UseCaseModule = module {
    singleOf(::ServerUseCase)
}
