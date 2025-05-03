package xyz.secozzi.jellyfinmanager.di

import org.jellyfin.sdk.createJellyfin
import org.jellyfin.sdk.model.ClientInfo
import org.jellyfin.sdk.model.DeviceInfo
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.BuildKonfig
import java.util.UUID

actual val JellyfinInstanceModule = module {
    single {
        createJellyfin {
            clientInfo = ClientInfo(
                name = BuildKonfig.NAME,
                version = BuildKonfig.VERSION,
            )
            deviceInfo = DeviceInfo(
                id = UUID.randomUUID().toString(),
                name = "jellyfin-manager",
            )
        }
    }
}
