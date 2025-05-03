package xyz.secozzi.jellyfinmanager.di

import org.jellyfin.sdk.createJellyfin
import org.jellyfin.sdk.model.ClientInfo
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.BuildKonfig

actual val JellyfinInstanceModule = module {
    single {
        createJellyfin {
            context = get()
            clientInfo = ClientInfo(
                name = BuildKonfig.NAME,
                version = BuildKonfig.VERSION,
            )
        }
    }
}
