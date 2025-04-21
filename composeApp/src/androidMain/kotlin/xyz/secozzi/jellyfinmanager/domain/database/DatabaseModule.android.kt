package xyz.secozzi.jellyfinmanager.domain.database

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.domain.db.JMDatabase
import xyz.secozzi.jellyfinmanager.util.DatabasePassword

actual val JMDatabaseModule = module {
    single {
        System.loadLibrary("sqlcipher")

        // From https://github.com/jobobby04/TachiyomiSY
        JMDatabase(
            AndroidSqliteDriver(
                schema = JMDatabase.Schema,
                context = get(),
                name = DB_NAME,
                factory = SupportOpenHelperFactory(DatabasePassword.getDatabasePassword(), null, false),
                callback = object : AndroidSqliteDriver.Callback(JMDatabase.Schema) {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        setPragma(db, "foreign_keys = ON")
                        setPragma(db, "journal_mode = WAL")
                        setPragma(db, "synchronous = NORMAL")
                    }

                    private fun setPragma(db: SupportSQLiteDatabase, pragma: String) {
                        val cursor = db.query("PRAGMA $pragma")
                        cursor.moveToFirst()
                        cursor.close()
                    }
                },
            )
        )
    }
}
