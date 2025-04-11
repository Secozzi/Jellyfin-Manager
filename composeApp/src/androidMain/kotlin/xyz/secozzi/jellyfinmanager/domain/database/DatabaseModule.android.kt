package xyz.secozzi.jellyfinmanager.domain.database

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.domain.db.JMDatabase

actual val JMDatabaseModule = module {
    single {
        JMDatabase(
            AndroidSqliteDriver(
                JMDatabase.Schema,
                get(),
                name = DB_NAME,
            )
        )
    }
}
