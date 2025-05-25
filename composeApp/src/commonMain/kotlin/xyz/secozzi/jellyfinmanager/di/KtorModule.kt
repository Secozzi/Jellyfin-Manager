package xyz.secozzi.jellyfinmanager.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

val KtorModule = module {
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(get())
            }
        }
    }
}
