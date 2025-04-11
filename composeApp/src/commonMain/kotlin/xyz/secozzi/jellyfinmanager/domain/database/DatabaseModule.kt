package xyz.secozzi.jellyfinmanager.domain.database

import org.koin.core.module.Module
import org.koin.dsl.module

val DatabaseModule = module {
    includes(
        JMDatabaseModule,
        DaosModule,
        UseCaseModule,
    )
}

const val DB_NAME = "jm.db"

expect val JMDatabaseModule: Module
