package xyz.secozzi.jellyfinmanager.domain.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.koin.dsl.module
import org.sqlite.mc.SQLiteMCSqlCipherConfig
import xyz.secozzi.jellyfinmanager.domain.db.JMDatabase
import xyz.secozzi.jellyfinmanager.getConfigDir
import xyz.secozzi.jellyfinmanager.util.Crypto
import java.io.File
import java.util.Properties

actual val JMDatabaseModule = module {
    single {
        val databasePath = File(getConfigDir(), DB_NAME)

        // From https://github.com/AChep/keyguard-app
        val sqlCipherProps = SQLiteMCSqlCipherConfig.getDefault()
            .withRawUnsaltedKey(Crypto.getDatabasePassword())
            .build()
            .toProperties()

        JMDatabase(
            JdbcSqliteDriver(
                url = "jdbc:sqlite:${databasePath.absolutePath}",
                properties = Properties().apply {
                    putAll(sqlCipherProps)
                    put("foreign_keys", "true")
                },
            ).also { driver ->
                JMDatabase.Schema.create(driver = driver)
            }
        )
    }
}
