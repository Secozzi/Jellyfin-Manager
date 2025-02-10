package xyz.secozzi.jellyfinmanager

import android.app.Application
import org.conscrypt.Conscrypt
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import xyz.secozzi.jellyfinmanager.di.initKoin
import java.security.Security

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Security.removeProvider("BC")
        Security.insertProviderAt(Conscrypt.newProvider(), 1)

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
