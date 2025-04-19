package xyz.secozzi.jellyfinmanager

import android.app.Application
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import xyz.secozzi.jellyfinmanager.di.initKoin
import java.security.Security

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Security.removeProvider("BC")
        Security.addProvider(BouncyCastleProvider())

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
