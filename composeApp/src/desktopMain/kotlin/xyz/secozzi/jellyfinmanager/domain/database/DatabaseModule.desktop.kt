package xyz.secozzi.jellyfinmanager.domain.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.domain.db.JMDatabase
import xyz.secozzi.jellyfinmanager.getConfigDir
import java.io.File

actual val JMDatabaseModule = module {
    single {
        val databasePath = File(getConfigDir(), DB_NAME)

        JMDatabase(
            JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}").also { driver ->
                JMDatabase.Schema.create(driver = driver)
            }
        )
    }
}
