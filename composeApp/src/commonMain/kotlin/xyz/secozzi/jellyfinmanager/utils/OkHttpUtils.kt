package xyz.secozzi.jellyfinmanager.utils

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import xyz.secozzi.jellyfinmanager.utils.ContinuationCallback.Companion.await
import java.io.IOException
import java.util.concurrent.TimeUnit.MINUTES
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val DEFAULT_CACHE_CONTROL = CacheControl.Builder().maxAge(10, MINUTES).build()
private val DEFAULT_HEADERS = Headers.Builder().build()
private val DEFAULT_BODY: RequestBody = FormBody.Builder().build()

class ContinuationCallback(
    private val call: Call,
    private val continuation: CancellableContinuation<Response>,
) : Callback, CompletionHandler {
    override fun onResponse(call: Call, response: Response) {
        continuation.resume(response)
    }

    override fun onFailure(call: Call, e: IOException) {
        if (!call.isCanceled()) {
            continuation.resumeWithException(e)
        }
    }

    override fun invoke(cause: Throwable?) {
        try {
            call.cancel()
        } catch (_: Throwable) {}
    }

    companion object {
        /**
         * Suspends the current coroutine,
         * performs the network call and resumes the coroutine with the response
         */
        suspend inline fun Call.await(): Response {
            return suspendCancellableCoroutine { continuation ->
                val callback = ContinuationCallback(this, continuation)
                enqueue(callback)
                continuation.invokeOnCancellation(callback)
            }
        }
    }
}

suspend fun OkHttpClient.get(
    url: HttpUrl,
    headers: Headers = DEFAULT_HEADERS,
    cache: CacheControl = DEFAULT_CACHE_CONTROL,
): Response {
    return newCall(
        Request.Builder()
            .url(url)
            .headers(headers)
            .cacheControl(cache)
            .build(),
    ).await()
}

suspend fun OkHttpClient.post(
    url: HttpUrl,
    headers: Headers = DEFAULT_HEADERS,
    body: RequestBody = DEFAULT_BODY,
    cache: CacheControl = DEFAULT_CACHE_CONTROL,
): Response {
    return newCall(
        Request.Builder()
            .url(url)
            .post(body)
            .headers(headers)
            .cacheControl(cache)
            .build(),
    ).await()
}

context(json: Json)
inline fun <reified T> T.toRequestBody(): RequestBody {
    return json.encodeToString(this).toRequestBody(
        "application/json".toMediaType(),
    )
}

context(json: Json)
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> Response.parseAs(): T {
    return json.decodeFromStream(body.byteStream())
}

context(json: Json)
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> Response.parseAs(serializer: KSerializer<T>): T {
    return json.decodeFromStream(serializer, body.byteStream())
}
