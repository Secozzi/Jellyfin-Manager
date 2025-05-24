package xyz.secozzi.jellyfinmanager

import android.app.Application
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration
import xyz.secozzi.jellyfinmanager.di.initKoin
import java.security.Security

@OptIn(KoinExperimentalAPI::class)
class AndroidApp : Application(), KoinStartup {
    override fun onKoinStartup(): KoinConfiguration {
        Security.removeProvider("BC")
        Security.addProvider(BouncyCastleProvider())

        return koinConfiguration {
            androidContext(this@AndroidApp)
            modules(
                initKoin(
                    datastorePath = filesDir.path,
                ),
            )
        }
    }
}
