package xyz.secozzi.jellyfinmanager.di

import me.sujanpoudel.utils.paths.appCacheDirectory
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.BuildKonfig
import java.io.File
import java.util.concurrent.TimeUnit

val NetworkModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(2, TimeUnit.MINUTES)
            .cache(
                Cache(
                    directory = File(appCacheDirectory(BuildKonfig.APP_ID).toString(), "network_cache"),
                    maxSize = 10L * 1024 * 1024, // 10 MiB
                ),
            )
            .build()
    }
}
