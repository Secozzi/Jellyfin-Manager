package xyz.secozzi.jellyfinmanager

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import xyz.secozzi.jellyfinmanager.di.initKoin

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(
                initKoin(
                    datastorePath = filesDir.path,
                ),
            )
        }
    }
}
